package com.dd.android.dailysimple.schedule.common.binder

import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


@BindingAdapter("thumbnailUrl")
fun thumbnailUrl(view: AppCompatImageView, url: String?) {
    if (url.isNullOrEmpty()) return
    Glide.with(view.context).load(url)
        .thumbnail(0.5f)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(view)
}


@BindingAdapter("circleThumbnailUrl")
fun circleThumbnailUrl(view: AppCompatImageView, url: String?) {
    if (url.isNullOrEmpty()) return
    Glide.with(view.context).load(url)
        .thumbnail(0.5f)
        .apply(RequestOptions.circleCropTransform())
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(view)
}