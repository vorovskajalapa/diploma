package com.example.iot_ha.utils

fun String.toBooleanState(): Boolean = this.equals("ON", ignoreCase = true)
