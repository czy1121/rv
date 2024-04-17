package me.reezy.cosmo.rv.loadmore

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Space
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import me.reezy.cosmo.statelayout.StateLayout

class LoadMoreStateView(context: Context) : LinearLayoutCompat(context) {

    companion object {
        var defaultTextColor: Int = (0xff999999).toInt()
        var defaultTextSize: Float = 14f
        var defaultTypeface: Typeface? = null
    }

    val vImage = AppCompatImageView(context)
    val vText = AppCompatTextView(context)

    init {
        layoutParams = FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER)
        orientation = VERTICAL
        gravity = Gravity.CENTER

        addView(vImage, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
        addView(Space(context), LayoutParams(LayoutParams.WRAP_CONTENT, (resources.displayMetrics.density * 10).toInt()))
        addView(vText, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))

        setTextStyle()
    }

    fun setTextStyle(color: Int = defaultTextColor, size: Float = defaultTextSize, typeface: Typeface? = defaultTypeface) {
        vText.setTextColor(color)
        vText.textSize = size
        vText.typeface = typeface
    }

    fun setFullHeight(isFull: Boolean) {
        val layout = parent as StateLayout
        if (isFull) {
            layout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        } else {
            layout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (resources.displayMetrics.density * 60).toInt())
        }
    }

    fun setImage(resId: Int) {
        vImage.setImageResource(resId)
    }

    fun setText(text: String, onClick: (View) -> Unit = {}) {
        vText.text = text
        vText.setOnClickListener(onClick)
    }

    fun setText(resId: Int, onClick: (View) -> Unit = {}) {
        vText.text = resources.getString(resId)
        vText.setOnClickListener(onClick)
    }
}