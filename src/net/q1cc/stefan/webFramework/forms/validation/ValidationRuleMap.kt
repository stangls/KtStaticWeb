package net.q1cc.stefan.webFramework.forms.validation

import java.util.*

open class ValidationRuleMap<T> (
        open val defaultValueFun : (idx:String) -> String,
        open val validateFun: ValidationRule<T>.(idx:String, T) -> ValidationResult,
        open val castFun: (idx:String, Any?)->T = { idx, it -> it as T }
) : HashMap<String,ValidationRule<T>>() {

    constructor(
            defaultValue : String,
            validateFun: ValidationRule<T>.(T) -> ValidationResult,
            castFun: (Any?)->T = { it as T }
    ) : this(
        {defaultValue},
        {idx,value -> validateFun(value)},
        {idx,it -> castFun(it) }
    )

    protected fun create( idx: String, defaultValue : String = defaultValueFun(idx) ) =
        ValidationRule<T>(
            defaultValue,
            { it: T -> validateFun(this, idx, it) },
            { castFun(idx, it) }
        )

    open fun getOrCreate(idx: String,  defaultValue : String = defaultValueFun(idx)) : ValidationRule<T>
        = getOrPut(idx) { create(idx,defaultValue) }

}


// TODO: parent class should have two generic parameters, There should be instances of int- & string-validation-rule
//       otherwise forEach for example iterates over wrong type :-(
open class IntValidationRuleMap<T> (
        val defaultIntValueFun: (idx:Int) -> String,
        validateFun: ValidationRule<T>.(idx:Int, T) -> ValidationResult,
        castFun: (idx:Int, Any?)->T = { idx, it -> it as T }
) : ValidationRuleMap<T>(
    { idx -> defaultIntValueFun(idx.toInt()) },
    { idx, v:T -> validateFun(idx.toInt(),v) },
    { idx, v:Any? -> castFun(idx.toInt(),v) }
) {
    constructor(
            defaultValue : String,
            validateFun: ValidationRule<T>.(T) -> ValidationResult,
            castFun: (Any?)->T = { it as T }
    ) : this(
            {defaultValue},
            {idx,value -> validateFun(value)},
            {idx,it -> castFun(it) }
    )
    open fun getOrCreate(idx: Int,  defaultValue : String = defaultIntValueFun(idx)) : ValidationRule<T>
        = super.getOrCreate(idx.toString(), defaultValue)
}
