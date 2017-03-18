package net.q1cc.stefan.webFramework.preseed

import net.q1cc.stefan.webFramework.dslHelpers.MaybeOtherwise
import net.q1cc.stefan.webFramework.dslHelpers.NoOtherwise
import net.q1cc.stefan.webFramework.dslHelpers.Otherwise
import net.q1cc.stefan.webFramework.redirect
import java.util.*

open class SessionData<User> (
    var user: User? = null,
    var lastMailSent: Date? = null
){

    inline fun withUser(with: (user: User) -> Unit) : MaybeOtherwise {
        val user = this.user
        if (user!=null){
            with(user)
            return NoOtherwise
        }else{
            return Otherwise
        }
    }

    fun requireUser() {
        if (!hasUser()){
            redirect("/")
        }
    }

    fun hasUser() = user!=null

    inline fun <X> withoutUser(function: () -> X) {
        if (user==null){ function() }
    }
}

