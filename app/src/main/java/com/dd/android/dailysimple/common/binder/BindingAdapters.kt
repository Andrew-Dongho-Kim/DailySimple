package com.dd.android.dailysimple.common.binder

import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.*
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dd.android.dailysimple.common.utils.setUnderlineText


@BindingAdapter("thumbnail")
fun thumbnailUrl(view: AppCompatImageView, url: String?) {
    if (url.isNullOrEmpty()) return
    Glide.with(view.context).load(url)
        .thumbnail(0.5f)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .into(view)
}


@BindingAdapter("circleThumbnail")
fun circleThumbnailUrl(view: AppCompatImageView, url: String?) {
    if (url.isNullOrEmpty()) return

    view.imageTintList = null
    Glide.with(view.context).load(url)
        .thumbnail(0.5f)
        .apply(RequestOptions.circleCropTransform())
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .into(view)
}

@BindingAdapter("circleThumbnail")
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

@BindingAdapter("onItemSelected")
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

@BindingAdapter("selectItemPosition")
fun selectItemPosition(spinner: AppCompatSpinner, position: Int) {
    spinner.setSelection(position)
}

@BindingAdapter("onCheckChanged")
fun onCheckChanged(compound: SwitchCompat, listener: CompoundButton.OnCheckedChangeListener) {
    compound.setOnCheckedChangeListener(listener)
}

@BindingAdapter("onCheckChanged")
fun onCheckChanged(compound: AppCompatCheckBox, listener: CompoundButton.OnCheckedChangeListener) {
    compound.setOnCheckedChangeListener(listener)
}

@BindingAdapter("onDateChanged")
fun onDateChanged(calendar: CalendarView, listener: CalendarView.OnDateChangeListener) {
    calendar.setOnDateChangeListener(listener)
}

@BindingAdapter("onTimeChanged")
fun onTimeChanged(timePicker: TimePicker, listener: TimePicker.OnTimeChangedListener) {
    timePicker.setOnTimeChangedListener(listener)
}

@BindingAdapter("onEditorAction")
fun onEditorAction(textView: TextView, listener: TextView.OnEditorActionListener) {
    textView.setOnEditorActionListener(listener)
}

@BindingAdapter("underlineText")
fun setUnderlineText(textView: TextView, text: String?) {
    text?.let { textView.setUnderlineText(it) }
}

@Suppress("deprecation")
@BindingAdapter("spannableText")
fun setSpannableText(textView: TextView, text: String?) {
    text?.let { textView.text = Html.fromHtml(it) }
}

interface OnTextChangeListener {
    fun onTextChanged(text: String?)
}

@BindingAdapter("onTextChanged")
fun onTextChanged(editText: AppCompatEditText, listener: OnTextChangeListener) {
    editText.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            listener.onTextChanged(s?.toString() ?: "")
        }
    })
}

