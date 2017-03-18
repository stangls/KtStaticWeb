package net.q1cc.stefan.webFramework.forms

import azagroup.kotlin.css.*
import azagroup.kotlin.css.colors.Color
import azagroup.kotlin.css.colors.rgb
import azagroup.kotlin.css.dimens.em
import azagroup.kotlin.css.dimens.px
import net.q1cc.stefan.webFramework.staticContent.CSS
import net.q1cc.stefan.webFramework.staticContent.CssClass

/**
 * Created by stefan on 18.11.16.
 */
object FormCss : CSS() {
    override fun getName() = "input"

    override fun style(): Stylesheet.() -> Unit = {
        input {
            padding = 0.5.em
            borderColor = rgb(50,50,100)
            borderWidth = 1.px
            borderStyle = SOLID
        }
        textarea {
            width = 35.em
            height = 20.em
            borderColor = rgb(50,50,100)
            borderWidth = 1.px
            borderStyle = SOLID
        }
        input["type","submit"] {
            backgroundColor = rgb(100,100,255)
            color = WHITE
        }
        input["type","file"] {
            border = NONE
        }
    }
    val form = CssClass(){
        td{
            verticalAlign = MIDDLE
            paddingBottom = 0.5.em
        }

    }
}