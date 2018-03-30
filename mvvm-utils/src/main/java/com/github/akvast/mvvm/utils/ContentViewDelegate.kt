package com.github.akvast.mvvm.utils

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.app.AppCompatActivity
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ContentViewDelegate<out T : ViewDataBinding?>(private val layoutId: Int)
    : ReadOnlyProperty<AppCompatActivity, T> {

    private var value: T? = null

    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        return value ?: DataBindingUtil.setContentView<T>(thisRef, layoutId).apply {
            value = this
        }
    }

}

fun <T : ViewDataBinding> contentView(layoutId: Int) = ContentViewDelegate<T>(layoutId)