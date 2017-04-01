package net.q1cc.stefan.webFramework

import azagroup.kotlin.css.Stylesheet
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.http.HttpServerRequest
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CookieHandler
import io.vertx.ext.web.handler.SessionHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.ext.web.sstore.ClusteredSessionStore
import io.vertx.ext.web.sstore.LocalSessionStore
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.styleLink
import net.q1cc.stefan.webFramework.ext.html
import net.q1cc.stefan.webFramework.ext.send
import net.q1cc.stefan.webFramework.forms.*
import net.q1cc.stefan.webFramework.mvp.*
import net.q1cc.stefan.webFramework.preseed.ISessionData
import net.q1cc.stefan.webFramework.staticContent.CSS
import java.io.File
import java.util.*

/**
 * Created by stefan on 11.10.16.
 */
class WebFramework<TSessionData : Any>(
        configuration: HttpServerOptions = HttpServerOptions(),
        sessionDataGenerator: (RoutingContext) -> TSessionData,
        formCss: CSS = FormCss,
        formValidationCss : CSS = ValidationCss,
        val publicDir : String = "public"
) {

    val vertx = Vertx.vertx()
    val server: HttpServer
    val router: Router
    val sessions: SessionFetcher<TSessionData>
    val mainCss = LinkedList<CSS>()
    val bodyHandler: BodyHandler

    init {
        server = vertx.createHttpServer(configuration)
        router = Router.router(vertx)
        bodyHandler = BodyHandler.create().setUploadsDirectory(createTempDir().absolutePath)
        bodyHandler.setBodyLimit(30*1000*1000)
        router.route().handler(bodyHandler)
        router.route().handler(CookieHandler.create())
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)))
        sessions = SessionFetcher<TSessionData>(sessionDataGenerator)
        addStyleSheet(formCss,true)
        addStyleSheet(formValidationCss,true)
    }

    fun route( path: String, block: RoutingContext.()->Unit ) {
        //router.route().path(path).handler(bodyHandler)
        router.route().path(path).blockingHandler({
            try {
                it.block()
            }catch( rr: RerouteException ) {
                it.reroute(rr.path)
            }catch( rd: RedirectException ) {
                with(it.response()) {
                    statusCode = 302
                    putHeader("Location",rd.path)
                    end()
                }
            }
        })

    }

    fun addStyleSheet(css: CSS, includeAlways: Boolean = false) {
        route("/css/${css.getName()}.css", {
            // TODO: maybe add some file-based caching
            response().send(
                Stylesheet {
                    css.getStyle()()
                }.render()
            )
        })
        if (includeAlways) {
            mainCss += css
        }
    }

    private fun getSessionData(ctx:RoutingContext): TSessionData {
        val sessionData = sessions[ctx]
        if (sessionData is ISessionData<*>)
            sessionData.onRequest(ctx)
        return sessionData
    }
    private fun  prepareRequest(ctx:RoutingContext) = getSessionData(ctx).to(parseRequestModel(ctx))

    fun present(
        path: String,
        view: IHtmlView<Unit,TSessionData>
    ) = present(path,NullModel,view)

    fun <IData> present(
        path: String,
        model: IModel<IData, TSessionData>,
        view: IHtmlView<IData,TSessionData>
    ) {
        // TODO: json via https://github.com/cbeust/klaxon
        val lambda: RoutingContext.() -> Unit = {
            val (sessionData,requestModel) = prepareRequest(this)
            handleRequestModel(requestModel,request(),sessionData,model)
            renderHtml(view, model.process(request(), sessionData),sessionData)()
            finishRequest()
        }
        getAndPost(lambda, path)
        // TODO: think about calling addStyleSheet(view.getIncludedCSS()) safely. - or at least when registering
    }

    private fun finishRequest() {
        FormRegistry.cleanup()
    }

    private fun getAndPost(lambda: RoutingContext.() -> Unit, path: String) {
        route(path, lambda)
        //server.post(path, lambda) // TODO: check if it works
    }


    fun <T> inside(model: IModel<T, TSessionData>, containerView: IHtmlContainerView<T, TSessionData>, path:String="/", function: ContainerPresenter<T>.() -> Unit) {
        ContainerPresenter(path,model,containerView).function()
    }

    inner class ContainerPresenter<T> internal constructor(val outerPath:String, val mOuter: IModel<T, TSessionData>, val containerView: IHtmlContainerView<T, TSessionData>) {
        fun present(
            path: String,
            view: IHtmlView<Unit, TSessionData>
        ) = present(path, NullModel,view)
        fun <IData> present(
            path: String,
            model: IModel<IData, TSessionData>,
            view: IHtmlView<IData, TSessionData>
        ) {
            val lambda: RoutingContext.() -> Unit = {
                val (sessionData,requestModel) = prepareRequest(this)
                handleRequestModel(requestModel,request(),sessionData,mOuter,model)
                // processing & rendering
                renderHtml(
                    combineViews(containerView,view),
                    mOuter.process(request(), sessionData).to(model.process(request(), sessionData)),
                    sessionData
                )()
                finishRequest()
            }
            getAndPost(lambda, outerPath.removeSuffix("/")+"/"+path.removePrefix("/"))
            // TODO: think about calling addStyleSheet(view.getIncludedCSS()) safely. - or at least when registering
        }
    }

    private fun handleRequestModel(requestModel: Any?, request: HttpServerRequest, sessionData: TSessionData, vararg targets : Any) {
        if (requestModel==null) return
        var handled = handleRequestModelAsForm(requestModel, request, sessionData)
        targets.forEach {
            handled = handled or callRequestModelMethod(it, requestModel, request, sessionData)
        }
        if (!handled){
            if (targets.isNotEmpty()) {
                val joined = targets.joinToString { it.javaClass.simpleName }
                System.err.println("Neither $joined nor form ${requestModel.javaClass.simpleName} handled ${requestModel.javaClass.simpleName}}")
            }else{
                System.err.println("The form ${requestModel.javaClass.simpleName} did not handle ${requestModel.javaClass.simpleName}}")
            }
        }
    }

    private fun <TData> renderHtml(view: IHtmlView<TData,TSessionData>, data: TData, sessionData : TSessionData): RoutingContext.() -> Unit = {
        response().html({
            head {
                mainCss.forEach {
                    styleLink("/css/${it.getName()}.css")
                }
                view.getIncludedCss().forEach {
                    styleLink("/css/${it.getName()}.css")
                }
            }
            body {
                view.render(data,sessionData)()
            }
        })
    }

    fun start() {
        router.route().handler(StaticHandler.create(publicDir))
        server.requestHandler({router.accept(it)}).listen()
    }


}

fun <T> minimalWebFramework( x : WebFramework<Unit>.() -> T) =
        with(WebFramework(sessionDataGenerator = { Unit }),x)