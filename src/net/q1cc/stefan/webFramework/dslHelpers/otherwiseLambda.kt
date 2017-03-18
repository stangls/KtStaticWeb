package net.q1cc.stefan.webFramework.dslHelpers

interface MaybeOtherwise {
    infix fun otherwise(otherwise: () -> Unit)
}

object NoOtherwise : MaybeOtherwise {
    override fun otherwise(otherwise: () -> Unit) {}
}
object Otherwise : MaybeOtherwise {
    override fun otherwise(otherwise: () -> Unit) {
        otherwise()
    }
}