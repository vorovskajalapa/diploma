package com.example.iot_ha.utils

fun String.toBooleanState(): Boolean = when (this) {
    "ON", "true", "1" -> true
    else -> false
}