package com.dd.android.dailysimple.common.binder

import android.view.View
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dd.android.dailysimple.common.setUnderlineText


@BindingAdapter("app:thumbnail")
fun thumbnailUrl(view: AppCompatImageView, url: String?) {
    if (url.isNullOrEmpty()) return
    Glide.with(view.context).load(url)
        .thumbnail(0.5f)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(view)
}


@BindingAdapter("app:circleThumbnail")
fun circleThumbnailUrl(view: AppCompatImageView, url: String?) {
    if (url.isNullOrEmpty()) return

    view.imageTintList = null
    Glide.with(view.context).load(url)
        .thumbnail(0.5f)
        .apply(RequestOptions.circleCropTransform())
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(view)
}

@BindingAdapter("app:circleThumbnail")
fun circleThumbnail(view: AppCompatImageView, @DrawableRes resId: Int) {
    if (resId == 0) return

    Glide.with(view.context).load(resId)
        .thumbnail(0.5f)
        .apply(RequestOptions.circleCropTransform())
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(view)
}


interface OnItemSelectedListener {
    fun onItemSelected(position: Int)
}

@BindingAdapter("app:onItemSelected")
fun onItemSelected(spinner: AppCompatSpinner, listener: OnItemSelectedListener?) {
    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>) {
            listener?.onItemSelected(-1)
        }

        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            listener?.onItemSelected(position)
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

@BindingAdapter("app:onDateChanged")
fun onDateChanged(calendar: CalendarView, listener: CalendarView.OnDateChangeListener) {
    calendar.setOnDateChangeListener(listener)
}

@BindingAdapter("app:onTimeChanged")
fun onTimeChanged(timePicker: TimePicker, listener: TimePicker.OnTimeChangedListener) {
    timePicker.setOnTimeChangedListener(listener)
}

@BindingAdapter("app:onEditorAction")
fun onEditorAction(textView: TextView, listener: TextView.OnEditorActionListener) {
    textView.setOnEditorActionListener(listener)
}

@BindingAdapter("app:underlineText")
fun setUnderlineText(textView: TextView, text: String?) {
    text?.let { textView.setUnderlineText(it) }
}
