package com.dd.android.dailysimple.common.binder

import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


@BindingAdapter("app:thumbnailUrl")
fun thumbnailUrl(view: AppCompatImageView, url: String?) {
    if (url.isNullOrEmpty()) return
    Glide.with(view.context).load(url)
        .thumbnail(0.5f)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(view)
}


@BindingAdapter("app:circleThumbnailUrl")
fun circleThumbnailUrl(view: AppCompatImageView, url: String?) {
    if (url.isNullOrEmpty()) return

    view.imageTintList = null
    Glide.with(view.context).load(url)
        .thumbnail(0.5f)
        .apply(RequestOptions.circleCropTransform())
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(view)
}


interface OnItemSelectedListener {
    fun onItemSelected(position: Int)
}

@BindingAdapter("app:onItemSelected")
fun onItemSelected(spinner: AppCompatSpinner, listener: OnItemSelectedListener) {
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>) {
            listener.onItemSelected(-1)
        }

        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            listener.onItemSelected(position)
        }
    }
}

@BindingAdapter("app:selectItemPosition")
fun selectItemPosition(spinner: AppCompatSpinner, position: Int) {
    spinner.setSelection(position)
}

@BindingAdapter("app:onCheckChanged")
fun onCheckChanged(compound: SwitchCompat, listener: CompoundButton.OnCheckedChangeListener) {
    compound.setOnCheckedChangeListener(listener)
}