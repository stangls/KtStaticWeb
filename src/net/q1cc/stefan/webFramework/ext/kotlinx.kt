package net.q1cc.stefan.webFramework.ext

import kotlinx.html.*
import net.q1cc.stefan.webFramework.forms.Form
import net.q1cc.stefan.webFramework.forms.FormRegistry
import net.q1cc.stefan.webFramework.staticContent.CssClass
import kotlin.reflect.KClass

/**
 * Created by stefan on 12.11.16.
 */


/**
 * allows the easy creation of forms, which have gone to the process of validation.
 * @see Form **/
fun <T : Form<T>> FlowContent.auto(
        form: KClass<T>,
        tabelize: Boolean = false,
        vararg params : Any,
        block: FORM.(T) -> Unit
) {
    // form from the request (or some other component that registered it for this request)
    val registeredForm = try{
        // this cast is required because generics are lost during runtime and the cast-exception would otherwise be thrown on another place
        form.java.cast(FormRegistry.get<T, Form<T>>())
    }catch(e:ClassCastException){
        null
    }
    // we just want to have some form of form
    val instance =
        registeredForm ?: form.objectInstance ?:
        if (params.isEmpty()) {
            form.java.newInstance() // todo: maybe cache empty instance for performance
        }else{
            form.java.getConstructor(*(params.map{ it.javaClass }.toTypedArray())).newInstance(*params)
        }
    if (instance == null) {
        Exception("can not create instance of type ${form}").printStackTrace()
        return
    }
    // supply arguments to the form and create the form
    instance.create(this,tabelize,block)
}

////// css-class casting
fun classesToString( vararg classes: CssClass? ) = classes.filterNotNull().joinToString(" ", transform = { it.name })
fun classesToStringSet( vararg classes: CssClass? ) = classes.mapNotNull { it?.name }.toSet()
////// css-class auto-casting (incomplete)
fun FlowContent.div(vararg classes: CssClass?, block: DIV.() -> Unit )
    = div(classesToString(*classes),block)
fun FlowContent.img(vararg classes: CssClass?, block: IMG.() -> Unit )
    = img(classes= classesToString(*classes),block=block)
fun FlowContent.span(vararg classes: CssClass?, block: SPAN.() -> Unit )
    = span(classes= classesToString(*classes),block=block)
fun FlowContent.a(vararg classes: CssClass?, block: A.() -> Unit )
    = a(classes= classesToString(*classes),block=block)
fun FlowContent.p(vararg classes: CssClass?, block: P.() -> Unit )
    = p(classes= classesToString(*classes),block=block)
fun FlowContent.table(vararg classes: CssClass?, block: TABLE.() -> Unit)
    = table(classes= classesToString(*classes),block=block)
fun TR.td(vararg classes: CssClass?, block: TD.() -> Unit)
    = td(classes=classesToString(*classes),block=block)
fun TABLE.tr(vararg classes: CssClass?, block: TR.() -> Unit)
    = tr(classes=classesToString(*classes),block=block)

val nbsp = Entities.nbsp