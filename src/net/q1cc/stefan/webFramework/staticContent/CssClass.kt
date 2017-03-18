package net.q1cc.stefan.webFramework.staticContent

import azagroup.kotlin.css.Stylesheet
import javax.swing.text.Style

/**
 * you can simply use it in CSS as:
 *   val myClass = CssClass("myClass"){...}
 */
data class CssClass(var name:String="", val parent: CssClass?=null, val body: Stylesheet.() -> Unit={}){
    constructor(name: String) : this(name,null,{})
    constructor(body:Stylesheet.()->Unit) : this("",body=body)
    constructor(name:String, body:Stylesheet.()->Unit) : this(name,parent=null,body=body)
    constructor(parent:CssClass, body:Stylesheet.()->Unit) : this(name="",parent=parent,body=body)
    constructor() : this("",body={})
    init{
        if (name.isEmpty()){
            name = "_c"+hashCode().toString()
        }
    }

    override fun toString(): String = name


    fun Stylesheet.define(body: (Stylesheet.()->Unit)) {
        c(name, {
            parent?.body?.invoke(this)
            body()
        })
    }
    operator fun invoke(s: Stylesheet){
        s.define { body() }
    }


}