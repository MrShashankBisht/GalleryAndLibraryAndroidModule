package com.iab.galleryandlibrary.utils

import android.content.Context
import android.util.DisplayMetrics

fun dpToPx(context: Context?, dp: Int): Int {
    return if (context != null) {
        // Get the screen's density scale
        val density = context.resources.displayMetrics.density
        // Convert the dps to pixels, based on density scale
        (dp * density + 0.5f).toInt()
    } else {
        -1
    }
}

fun pxToDp(context: Context, px: Float): Float {
    return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}