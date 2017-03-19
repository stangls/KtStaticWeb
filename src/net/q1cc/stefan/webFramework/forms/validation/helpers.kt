package net.q1cc.stefan.webFramework.forms.validation

import net.q1cc.stefan.webFramework.reflection.getGetters

fun <T> verify(validateFun: ValidationRule<T>.(T) -> Boolean, errorMessage: String ): ValidationRule<T>.(T) -> ValidationResult = {
    if (validateFun(it)){
        ValidationOk
    }else{
        ValidationFailed(errorMessage)
    }
}

fun verifyEmail( errorMessage: String ) = verify<String>({
    val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$".toRegex()
    it.matches(emailRegex)
},errorMessage)

fun Any.getValidationRules() = getGetters<ValidationRule<*>>()
fun Any.getHiddenFields() = getGetters<HiddenField<*>>()
fun Any.getRuleLists() = getGetters<ValidationRuleMap<*>>()
//fun Any.getRuleMaps() = getGetters<ValidationRuleMap<*,*>>()
fun Any.getRuleCollectionsFlat() = getRuleLists().flatten() //+ getRuleMaps().flatten()
fun Any.getRulesRecursive() = getValidationRules() + getRuleCollectionsFlat()
fun Any.getAdditionalButtons() = getGetters<AdditionalSubmit>()

internal fun List<Pair<String, *>>.flatten() : List<Pair<String,ValidationRule<*>>> = flatMap {
    val (n,l) = it
    return when(l){
        is List<*> -> l.mapIndexed { i, it -> "$n[$i]".to(it as ValidationRule<*>) }
        is Map<*,*> -> l.map { val (k,v) = it; "$n[$k]".to(v as ValidationRule<*>) }
        else -> listOf()
    }
}
