package net.q1cc.stefan.webFramework.forms.validation

import kotlinx.html.InputType
import net.q1cc.stefan.webFramework.forms.ValidationCss
import net.q1cc.stefan.webFramework.reflection.getGetters
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * Created by stefan on 12.11.16.
 */
open class ValidationRule<T>(
        open var defaultValue : String,
        open val validateFun: ValidationRule<T>.(T) -> ValidationResult,
        open val castFun: (Any?)->T = { it as T }
) {
    constructor(validateFun : ValidationRule<T>.(T) -> ValidationResult, castFun: (Any?)->T ) : this("",validateFun, castFun)
    constructor( validateFun : ValidationRule<T>.(T) -> ValidationResult) : this("",validateFun)
    constructor( defaultValue : String ) : this(defaultValue,{ ValidationOk })
    constructor() : this({ ValidationOk })

    var valueWasSet = false
        private set
    internal var value: Any? = null
        set(value) {
            if (!valueWasSet) {
                valueWasSet = true
                field = value
            }
        }

    internal var cachedValidationResult : ValidationResult? = null
    private var valueWasSetWhenCachingValidationResult = false
    /**
     * caches validation for performance improvements (as long as value is not changed).
     *
     * value should actually only be changed by internal means, so this result should always be cached for normal framework users.
     */
    open fun getResult(): ValidationResult {
        // caching part 1 (read)
        val cvr = cachedValidationResult
        if (cvr!=null && valueWasSet==valueWasSetWhenCachingValidationResult) {
            return cvr
        }
        // actual computation
        val ret =
            try {
                val value = castFun(value)
                validateFun(value)
            } catch (e: ClassCastException) {
                ValidationFailed("internal error (can not cast)")
            } catch (e: IllegalArgumentException) {
                ValidationFailed("internal error (illegal argument)")
            } catch (e: Exception) {
                ValidationFailed(e.message ?: "${value?.javaClass?.simpleName ?: "null"} not validatable")
            }
        // caching part 2 (write)
        cachedValidationResult = ret
        valueWasSetWhenCachingValidationResult = valueWasSet
        return ret
    }

    open fun isValid(): Boolean = getResult() == ValidationOk

    open val inputType = InputType.text
    open protected val canonicalName: String = "vr_" + hashCode().toString()
    fun canonicalName(t:Any?) : String {
        t?.getRulesRecursive()?.forEach {
            try {
                if (it.second === this) {
                    return it.first
                }
            }catch(e:Exception){}
        }
        return canonicalName
    }

    /**
     * use this to access the assigned value type-safely,
     * i.e. it is null if it could not be validated.
     *
     * this may be especially useful if validation-rules depend on each other (non-recursively).
     */
    fun getValidatedValue() : T? {
        if (getResult()== ValidationOk){
            // should be ok since getResult checks this cast
            return castFun(value)
        }else{
            return null
        }
    }

    open fun getValueForForm(): String =
        if (value!=null){
            value.toString()
        }else{
            defaultValue
        }

    fun classes() : String =
        if (valueWasSet) {
            // show validation
            if (isValid()) ValidationCss.valid.name
            else ValidationCss.invalid.name
        } else {
            ""
        }
}