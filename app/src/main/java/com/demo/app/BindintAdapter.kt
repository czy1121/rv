package com.demo.app

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

@BindingAdapter("src")
fun BindingAdapter_src(view: ImageView, value: String?) {
    view.load(value ?: return)
}