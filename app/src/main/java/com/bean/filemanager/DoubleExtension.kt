package com.bean.filemanager

import java.math.RoundingMode

fun Double.decimal(places: Int): Double{
    return this.toBigDecimal().setScale(places, RoundingMode.HALF_UP).toDouble()
}