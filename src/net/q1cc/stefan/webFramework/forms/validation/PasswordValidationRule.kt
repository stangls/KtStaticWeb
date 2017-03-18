package net.q1cc.stefan.webFramework.forms.validation

import kotlinx.html.InputType

open class PasswordValidationRule<T>(vFun: ValidationRule<T>.(T) -> ValidationResult) : ValidationRule<T>(vFun){
    constructor() : this({ ValidationOk })
    override val inputType = InputType.password
    override fun getValueForForm(): String = ""
}