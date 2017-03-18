package net.q1cc.stefan.webFramework.forms.validation

interface ValidationResult {}
object ValidationOk : ValidationResult
class ValidationFailed(vararg val messages:String) : ValidationResult {
    override fun toString(): String
            = "Failed: ${messages.joinToString()}"
}