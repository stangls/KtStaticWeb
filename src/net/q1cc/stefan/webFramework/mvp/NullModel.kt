package net.q1cc.stefan.webFramework.mvp

import io.vertx.core.http.HttpServerRequest
import io.vertx.ext.web.RoutingContext

object NullModel: IModel<Unit, Any> {
    override fun process(request: HttpServerRequest, session: Any): Unit {}
}