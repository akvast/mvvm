package com.github.akvast.mvvm.utils

import android.databinding.BindingAdapter
import android.net.Uri
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.common.RotationOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import java.io.File

object ViewModelUtils {
    var maxImagesWidth = 480
    var maxImagesHeight = 720
}

@BindingAdapter("goneIf")
fun bindGoneIf(view: View, hide: Boolean) {
    view.visibility = when (hide) {
        true -> View.GONE
        false -> View.VISIBLE
    }
}

@BindingAdapter("invisibleIf")
fun bindInvisibleIf(view: View, hide: Boolean) {
    view.visibility = when (hide) {
        true -> View.INVISIBLE
        false -> View.VISIBLE
    }
}

@BindingAdapter("android:src", "android:tint")
fun bindTintedImage(imageView: ImageView, @DrawableRes drawableId: Int, @ColorInt color: Int) {
    if (drawableId == 0) {
        imageView.setImageDrawable(null)
        return
    }

    val drawable = ResourcesCompat.getDrawable(imageView.context.resources, drawableId, null)
    if (drawable == null) {
        imageView.setImageDrawable(null)
        return
    }

    DrawableCompat.setTint(drawable, color)
    imageView.setImageDrawable(drawable)
}

@BindingAdapter("adapter")
fun bindRecyclerViewAdapter(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>) {
    recyclerView.adapter = adapter
    recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
}

@BindingAdapter("android:src")
fun bindDraweeImage(view: SimpleDraweeView, data: Any?) {
    val uri = when (data) {
        is String -> Uri.parse(data)
        is File -> Uri.fromFile(data)
        else -> {
            view.setImageURI(null as String)
            return
        }
    }

    var width = Math.min(ViewModelUtils.maxImagesWidth, view.getMeasuredWidth())
    var height = Math.min(ViewModelUtils.maxImagesHeight, view.getMeasuredHeight())

    if (width == 0) width = ViewModelUtils.maxImagesWidth
    if (height == 0) height = ViewModelUtils.maxImagesHeight

    val request = ImageRequestBuilder.newBuilderWithSource(uri)
            .setRotationOptions(RotationOptions.autoRotate())
            .setProgressiveRenderingEnabled(true)
            .setResizeOptions(ResizeOptions(width, height))
            .build()

    view.controller = Fresco.newDraweeControllerBuilder()
            .setImageRequest(request)
            .setOldController(view.controller)
            .build()
}