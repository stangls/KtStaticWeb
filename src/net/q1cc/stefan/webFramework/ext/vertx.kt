package net.q1cc.stefan.webFramework.ext

import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import kotlinx.html.HTML
import kotlinx.html.html
import kotlinx.html.stream.appendHTML

/**
 * Created by stefan on 06.11.16.
 */

fun HttpServerResponse.html(block: HTML.() -> Unit, prettyPrint: Boolean = true) =
    send(
        contentType = "text/html",
        output = StringBuilder().appendHTML(prettyPrint)
            .html(block)
            .toString()
    )

fun HttpServerResponse.send(output: String, contentType : String = "text/plain") {
    putHeader("content-type",contentType)
    end(output)
}
