package net.q1cc.stefan.webFramework.mvp

import kotlinx.html.BODY
import kotlinx.html.HTML

interface IHtmlContainerView<in TData, TSessionData> {
    fun render(model: TData, session:TSessionData, renderInner: BODY.() -> Unit): BODY.() -> Unit
}
fun <TData1,TData2,TSessionData> combineViews(v1: IHtmlContainerView<TData1,TSessionData>, v2: IHtmlView<TData2,TSessionData>): IHtmlView<Pair<TData1, TData2>,TSessionData> = object: IHtmlView<Pair<TData1, TData2>,TSessionData> {
    override fun render(model: Pair<TData1, TData2>, session: TSessionData): BODY.() -> Unit = {
        v1.render(model.first,session,v2.render(model.second, session))()
    }
}