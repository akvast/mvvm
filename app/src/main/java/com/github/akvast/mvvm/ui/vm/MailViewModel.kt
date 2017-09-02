package com.github.akvast.mvvm.ui.vm

import android.text.SpannableString
import android.text.style.ForegroundColorSpan

data class MailViewModel(val title: String,
                         val sender: String,
                         val message: String) {

    fun getContent(): SpannableString {
        val builder = SpannableString("$sender - $message")
        builder.setSpan(ForegroundColorSpan(0xFF000000.toInt()), 0, sender.length, 0)
        return builder
    }

}