package net.q1cc.stefan.webFramework.dslHelpers

/**
 * Created by stefan on 27.11.16.
 */

inline fun noExc(block: () -> Unit): Boolean =
        try {
            block()
            true
        }catch(e:Exception) {
            false
        }catch(e:Error) {
            false
        }

inline fun <T> noExc(block: () -> T): T? =
        try {
            block()
        }catch(e:Exception) {
            null
        }catch(e:Error) {
            null
        }


inline fun printExc(block: () -> Unit): Boolean =
        try {
            block()
            true
        }catch(e:Exception) {
            e.printStackTrace()
            false
        }catch(e:Error) {
            e.printStackTrace()
            false
        }

inline fun <T> printExc(block: () -> T): T? =
        try {
            block()
        }catch(e:Exception) {
            e.printStackTrace()
            null
        }catch(e:Error) {
            e.printStackTrace()
            null
        }

