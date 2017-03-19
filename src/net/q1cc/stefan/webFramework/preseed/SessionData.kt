package net.q1cc.stefan.webFramework.preseed

import io.vertx.ext.web.RoutingContext
import net.q1cc.stefan.webFramework.dslHelpers.MaybeOtherwise
import net.q1cc.stefan.webFramework.dslHelpers.NoOtherwise
import net.q1cc.stefan.webFramework.dslHelpers.Otherwise
import net.q1cc.stefan.webFramework.redirect

interface ISessionData<User> {
    var user: User?

    fun withUser(with: (user: User) -> Unit) : MaybeOtherwise {
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

    fun <X> withoutUser(function: () -> X) {
        if (user==null){ function() }
    }

    fun onRequest(ctx: RoutingContext) {}
}

open class SessionData<User> (
    override var user: User? = null
) : ISessionData<User>
