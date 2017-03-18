package net.q1cc.stefan.webFramework.forms

import azagroup.kotlin.css.HIDDEN
import kotlinx.html.*
import net.q1cc.stefan.webFramework.dslHelpers.NoOtherwise
import net.q1cc.stefan.webFramework.dslHelpers.Otherwise
import net.q1cc.stefan.webFramework.ext.*
import net.q1cc.stefan.webFramework.forms.validation.*
import java.io.File
import kotlin.reflect.primaryConstructor

/**
 * inherit from this class to define a form, which can be
 * * easily instantiated in views
 * * validated
 * * passed to models
 */
abstract class Form<T>(
    val submitText: String = "OK"
) {

    init{
        val kClass = this.javaClass.kotlin
        if (kClass.primaryConstructor==null){
            if (kClass.objectInstance != null){
                throw ExceptionInInitializerError("Sorry, but a form (currently) can not be an object!")
            }
            throw ExceptionInInitializerError("Sorry, but all forms must have a primary constructor (for now)!")
        }
    }

    override fun toString(): String =
        "${javaClass.simpleName}[" +
            ( getValidationRules() + getRuleCollectionsFlat() )
            .map { "${it.first} = ${it.second.value}" }.joinToString(" ")+
        " ]"

    /**
     * optional request URL used for attribute <form action="..."
     * to always redirect the POST-method to a specified model and view.
     */
    open val requestURL : String? = null
    /**
     * optional handler definition.
     *
     * when supplied, requests are always processed by this handler first
     * before being passed on to the actual model and view of the request URL.
     */
    open val requestHandler : Any? = null

    /** should the content be wrapped in a table (by default)? **/
    open var tabelize: Boolean = false

    /** internally used to construct the form **/
    protected var formTag: FORM? = null
    /** internally used to construct a table in the form **/
    protected var tableTag: TABLE? = null
    /** this instance as correct type **/
    protected fun getT() = this as T

    val validationProblems: Map<ValidationRule<*>, ValidationResult> by lazy{
        (getValidationRules()+ getRuleCollectionsFlat())
            .map { it.second.to(it.second.getResult()) }
            .filter { it.second != ValidationOk }
            .associate { it }
    }
    private var manualInvalidation: String? = null

    fun isValid() = validationProblems.isEmpty() && manualInvalidation==null
    fun valid(body: () -> Unit) =
        if (isValid()){
            body()
            NoOtherwise
        }else{
            Otherwise
        }

    fun invalidate(reason: String) {
        manualInvalidation = reason
    }

    /** view-function **/
    fun create(
        fc : FlowContent,
        tabelize: Boolean = false,
        func : FORM.(T)->Unit
    ){
        fc.form {
            formTag = this
            method = FormMethod.post
            encType = FormEncType.multipartFormData
            val actionURL = requestURL
            if (actionURL!=null){
                action = actionURL
            }
            input {
                type = InputType.hidden
                name = "_requestModelClass"
                value = this@Form.javaClass.canonicalName
            }
            fun render(){
                this@Form.getHiddenFields().forEach {
                    input(it.second){
                        type= InputType.hidden
                    }
                }
                func(getT())
                submit({})
            }
            val invMsg = manualInvalidation
            if (invMsg!=null){
                //div(listOf(ValidationCss.invalid.name,ValidationCss.spaced.name).joinToString(" ")) {
                div(ValidationCss.invalid,ValidationCss.spaced) {
                    +invMsg
                }
            }
            if (tabelize){
                table(FormCss.form) {
                    tableTag = this
                    render()
                }
            }else {
                tableTag = null
                render()
            }
        }
        reset()
    }

    protected fun reset() {
        formTag = null
        submitCreated = false
    }

    protected var submitCreated: Boolean = false
    fun submit(block : INPUT.() -> Unit = {}) =
        maybeTabelized {
            if (!submitCreated){
                formTag?.submitInput {
                    value = submitText
                    block()
                }
                submitCreated = true
            }
        }

    fun noSubmit() {
        submitCreated = true
    }

    fun submit(btn: AdditionalSubmit, block : INPUT.() -> Unit = {}) {
        maybeTabelized {
            formTag?.submitInput {
                value = btn.text
                name = btn.canonicalName(getT())
                block()
            }
        }
    }

    fun maybeTabelized( label:String?=null, body : ()->Unit ){
        tableTag?.tr {
            if (label!=null) td { +(label+":");+nbsp }
            td {
                if (label==null) colSpan = "2"
                body()
            }
        } ?: body()
    }

    fun input(vr : ValidationRule<*>, label : String? = null, block : INPUT.() -> Unit = {} ) =
        maybeTabelized(label) {
            formTag?.input(
                    name = vr.canonicalName(getT()),
                    type = vr.inputType,
                    classes = vr.classes(),
                    block = {
                        // show value
                        value = vr.getValueForForm()
                        // user block ( may change everything )
                        block()
                    }
            )
        }

    fun inputFile(vr: ValidationRule<File>, label : String? = null, block : INPUT.() -> Unit = {} ) =
        maybeTabelized(label) {
            formTag?.fileInput(
                name    = vr.canonicalName(getT()), classes = vr.classes(),
                block   = {
                    block()
                }
            )
        }

    fun textArea(vr: ValidationRule<String>, label : String? = null, block : TEXTAREA.() -> Unit = {} ) =
        maybeTabelized(label) {
            formTag?.textArea(
                classes = vr.classes()
            ) {
                name = vr.canonicalName(getT())
                // user block ( may change everything )
                block()
                // show value
                + vr.getValueForForm()
            }
        }


}