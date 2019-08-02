package com.github.akvast.mvvm.utils

import android.net.Uri
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import android.view.View
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.common.RotationOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import java.io.File
import kotlin.math.min

object ViewModelUtils {
    var maxImagesWidth = 480
    var maxImagesHeight = 720
}

@BindingAdapter("layout_width")
fun bindLayoutWidth(view: View, width: Float) {
    view.layoutParams.width = width.toInt()
}

@BindingAdapter("layout_height")
fun bindLayoutHeight(view: View, height: Float) {
    view.layoutParams.height = height.toInt()
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

@BindingAdapter("activity")
fun bindToolbarActivity(toolbar: Toolbar, activity: AppCompatActivity?) {
    activity?.setSupportActionBar(toolbar)
}

@BindingAdapter("src")
fun bindDraweeImage(view: SimpleDraweeView, data: Any?) {
    val uri = when (data) {
        is String -> Uri.parse(data)
        is File -> Uri.fromFile(data)
        else -> {
            view.setImageURI(null as String?)
            return
        }
    }

    var width = min(ViewModelUtils.maxImagesWidth, view.measuredWidth)
    var height = min(ViewModelUtils.maxImagesHeight, view.measuredHeight)

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