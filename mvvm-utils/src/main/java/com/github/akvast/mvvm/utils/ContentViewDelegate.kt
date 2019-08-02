package com.github.akvast.mvvm.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
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