package net.q1cc.stefan.webFramework.forms

import java.lang.ref.Reference
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Created by stefan on 14.11.16.
 */
internal object FormRegistry {

    /**
     * maps threads to the one form created from request
     */
    val requestForms = ConcurrentHashMap< Long, WeakReference<Form<*>> >()

    fun <X,T : Form<X>> get() : T? {
        val value = requestForms[Thread.currentThread().id]?.get()
        // cast can be unsafe, because we guarantee its safeness internally
        return value as T?
    }

    private val random = Random()

    private var cleanupProb: Int = 10

    fun <T : Form<*>> set(value: T) {
        val weakReference: WeakReference<Form<*>> = WeakReference(value)
        requestForms.put(Thread.currentThread().id, weakReference)
        // this should actually not be neccessary
        if (random.nextInt(cleanupProb)==0){
            cleanupAny()
        }
    }

    /** removes dead references from the map **/
    fun cleanupAny(){
        val preSize = requestForms.size
        requestForms.forEach(8,{ key, value ->
            if (value?.get()==null){
                requestForms.remove(key)
            }
        })
        if (preSize == requestForms.size){
            println("cleanup was actually not neccessary")
            cleanupProb*=2
        }else{
            println("cleanup was neccessary :-O")
            cleanupProb=Math.max(cleanupProb/5,1)
        }
    }

    fun cleanup(){
        requestForms.remove(Thread.currentThread().id)
    }

}

