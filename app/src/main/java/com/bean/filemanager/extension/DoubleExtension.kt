package com.bean.filemanager.extension

import java.math.RoundingMode

fun Double.decimal(places: Int): Double{
    return this.toBigDecimal().setScale(places, RoundingMode.HALF_UP).toDouble()
}