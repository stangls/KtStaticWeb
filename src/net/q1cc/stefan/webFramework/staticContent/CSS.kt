package net.q1cc.stefan.webFramework.staticContent

import azagroup.kotlin.css.Stylesheet
import kotlinx.html.HTML
import kotlinx.html.head
import kotlinx.html.styleLink
import net.q1cc.stefan.webFramework.mvp.IHtmlView
import net.q1cc.stefan.webFramework.reflection.getGetters

/**
 * A CSS file to be used together with Presenter.
 * <p>
 *     see functions AppServer.addStyleSheet and present
 * </p>
 */
abstract class CSS {
    abstract fun getName() : String
    open fun style() : Stylesheet.() -> Unit = {}
    fun getStyle() : Stylesheet.() -> Unit = {
        style()()
        this@CSS.getGetters<CssClass>().map{it.second}.forEach {
            it(this)
        }
    }

}
fun Stylesheet.include(css: CSS) = css.getStyle()()