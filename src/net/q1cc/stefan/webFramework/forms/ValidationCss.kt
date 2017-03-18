package net.q1cc.stefan.webFramework.forms

import azagroup.kotlin.css.*
import azagroup.kotlin.css.colors.rgb
import azagroup.kotlin.css.dimens.em
import azagroup.kotlin.css.dimens.px
import net.q1cc.stefan.webFramework.staticContent.CSS
import net.q1cc.stefan.webFramework.staticContent.CssClass

/**
 * Created by stefan on 15.11.16.
 */
object ValidationCss : CSS() {
    override fun getName(): String = "validation"

    val valid = CssClass(){
        borderWidth = 0.5.px
        borderColor = rgb(0, 100, 0)
        borderStyle = SOLID
        backgroundColor = rgb(200, 255, 200)
    }
    val invalid = CssClass(){
        borderWidth = 0.5.px
        borderColor = rgb(200, 0, 0)
        borderStyle = SOLID
        backgroundColor = rgb(255, 200, 200)
    }
    val spaced = CssClass(){
        padding = 1.em
        margin = 1.em
    }

}