package net.q1cc.stefan.webFramework.forms.validation

import net.q1cc.stefan.webFramework.reflection.getGetters

open class AdditionalSubmit( val text : String ) {
    open protected val canonicalName: String = "additionalSubmit_" + hashCode().toString()
    fun canonicalName(t:Any?) : String {
        t?.getAdditionalButtons()?.forEach {
            try {
                if (it.second === this) {
                    return it.first
                }
            }catch(e:Exception){}
        }
        return canonicalName
    }

    var clicked: Boolean = false

}