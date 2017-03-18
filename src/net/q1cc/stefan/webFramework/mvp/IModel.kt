package net.q1cc.stefan.webFramework.mvp

import io.vertx.core.http.HttpServerRequest
import io.vertx.ext.web.RoutingContext

/**
 * a controller does some logical thing by processing the request.
 * then puts the result into a model (which is then presented by a view).
 */
interface IModel<out TData, in TSessionData> {
    fun process(request: HttpServerRequest, session: TSessionData): TData
}