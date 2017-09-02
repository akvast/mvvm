package com.github.akvast.mvvm.ui.adapter

import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.Toast
import com.github.akvast.mvvm.BR
import com.github.akvast.mvvm.R
import com.github.akvast.mvvm.adapter.ViewModelAdapter
import com.github.akvast.mvvm.ui.vm.MailViewModel

object MainAdapter : ViewModelAdapter() {

    init {
        cell(String::class.java, R.layout.cell_header, BR.vm)
        cell(MailViewModel::class.java, R.layout.cell_mail, BR.vm)

        sharedObject(this, BR.adapter)
    }

    override fun reload(refreshLayout: SwipeRefreshLayout?) {
        items.clear()
        items.add("Today")
        items.add(MailViewModel("Brunch this weekend?", "Ali Connors", "I'll be in your neighborhood doing errands this weekend. Do you want..."))
        items.add(MailViewModel("Summer BBQ", "to Alex, Scott, Jennifer", "Wish I could come, but i'm out of town this weekend."))
        items.add("Yesterday")
        items.add(MailViewModel("Oui Oui", "Sandra Adams", "Do you have Paris recommendations? Have you even been?"))
        notifyDataSetChanged()
    }

    fun mailSelected(view: View, model: MailViewModel) {
        Toast.makeText(view.context, "${model.title} selected.", Toast.LENGTH_SHORT).show()
    }

}