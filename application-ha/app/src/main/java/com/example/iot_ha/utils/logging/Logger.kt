package com.example.iot_ha.utils.logging

import android.util.Log
import kotlin.reflect.KClass

object Logger {
    fun log(className: KClass<*>, message: String) {
        Log.i(className.simpleName, message)
    }

    fun log(tag: String, message: String) {
        Log.i(tag, message)
    }
}
