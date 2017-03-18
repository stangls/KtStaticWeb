package net.q1cc.stefan.webFramework.mvp

import kotlinx.html.BODY

interface IUHtmlView<T> : IHtmlView<Unit, T> {
    override fun render(model: Unit, session: T): BODY.() -> Unit = render(session)
    fun render(session:T): BODY.() -> Unit
}