package me.reezy.cosmo.rv.loadmore

import android.graphics.Typeface

class LoadMoreTextStyle(
    val color: Int = defaultTextColor,
    val size: Float = defaultTextSize,
    val typeface: Typeface? = defaultTypeface
) {

    companion object {
        var defaultTextColor: Int = (0xff999999).toInt()
        var defaultTextSize: Float = 14f
        var defaultTypeface: Typeface? = null
    }
}