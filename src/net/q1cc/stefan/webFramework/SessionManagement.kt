package net.q1cc.stefan.webFramework


import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerRequest
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.Session
import io.vertx.ext.web.handler.CookieHandler
import io.vertx.ext.web.handler.SessionHandler
import io.vertx.ext.web.sstore.ClusteredSessionStore
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by stefan on 06.11.16.
 */

class SessionFetcher<T> internal constructor(val initGenerator: (RoutingContext) -> T ) {

    operator fun get(context:RoutingContext) : T {
        return context.session().getOrPut( "KtStaticWeb_session", { initGenerator(context) } )
    }

}

private fun <T> Session.getOrPut(key: String, generator: () -> T): T {
    var ret = get<T>(key)
    if (ret==null){
        ret = generator()
        put(key,ret)
    }
    return ret
}
