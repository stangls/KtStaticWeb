package net.q1cc.stefan.webFramework.reflection

fun Class<*>.allClasses() : List<Class<*>> =
    listOf(this) +
    if (superclass!=null){
        listOf(this.superclass)
    }else{
        listOf()
    }