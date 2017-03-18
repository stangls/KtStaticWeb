package net.q1cc.stefan.webFramework.forms.validation

import net.q1cc.stefan.webFramework.forms.validation.ValidationResult
import net.q1cc.stefan.webFramework.forms.validation.ValidationRule
import net.q1cc.stefan.webFramework.reflection.getGetters

/**
 * Created by stefan on 11.12.16.
 */

class HiddenField<T>(
        defaultValue: String,
        validateFun: (T) -> ValidationResult,
        castFunct: (String) -> T = { it as T }
) : ValidationRule<T>(
    defaultValue = defaultValue,
    validateFun = { validateFun(it) },
    castFun = { castFunct(it as String) }
)