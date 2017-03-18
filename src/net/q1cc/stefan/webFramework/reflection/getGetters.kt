package net.q1cc.stefan.webFramework.reflection

/**
 * Returns a list of pairs ( name, value ) for each getter of type T
 *
 */
inline fun <reified T:Any> Any.getGetters(): List<Pair<String, T>> = getGetters(T::class.java)

fun <T> Any.getGetters( clazz : Class<T> ): List<Pair<String, T>> =
    javaClass.allClasses()
    .flatMap { it.declaredMethods.asIterable() }
    .filter {
        clazz.isAssignableFrom(it.returnType) &&
                it.parameterCount == 0 &&
                it.name.startsWith("get")
    }
    .mapNotNull {
        try {
            val name = it.name!!.removePrefix("get").decapitalize()
            val value = it.invoke(this) as T?
            if (value == null) {
                null
            } else {
                name.to(value)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

