package net.q1cc.stefan.webFramework.mvp

import kotlinx.html.BODY
import kotlinx.html.FORM
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import net.q1cc.stefan.webFramework.forms.Form
import net.q1cc.stefan.webFramework.staticContent.CSS
import kotlin.reflect.KClass

/**
 * A view to render HTML.
 */
interface IHtmlView<in TData, TSessionData> {
    fun render(model: TData, session: TSessionData): BODY.() -> Unit
    fun getIncludedCss() = setOf<CSS>()
}

