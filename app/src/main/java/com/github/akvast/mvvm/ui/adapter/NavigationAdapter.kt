package com.github.akvast.mvvm.ui.adapter

import android.view.View
import android.widget.Toast
import com.github.akvast.mvvm.BR
import com.github.akvast.mvvm.R
import com.github.akvast.mvvm.adapter.ViewModelAdapter
import com.github.akvast.mvvm.ui.vm.NavigationHeaderViewModel
import com.github.akvast.mvvm.ui.vm.NavigationItemViewModel
import com.github.akvast.mvvm.ui.vm.NavigationSubheaderViewModel

object NavigationAdapter : ViewModelAdapter() {

    init {
        cell(NavigationHeaderViewModel::class.java, R.layout.cell_navigation_header, BR.vm)
        cell(NavigationItemViewModel::class.java, R.layout.cell_navigation_item, BR.vm)
        cell(NavigationSubheaderViewModel::class.java, R.layout.cell_navigation_subheader, BR.vm)

        sharedObject(this, BR.adapter)

        items = arrayOf(
                NavigationHeaderViewModel,
                NavigationItemViewModel(R.drawable.ic_inbox_black_24dp, "Inbox"),
                NavigationItemViewModel(R.drawable.ic_star_black_24dp, "Starred"),
                NavigationItemViewModel(R.drawable.ic_send_black_24dp, "Sent mail"),
                NavigationItemViewModel(R.drawable.ic_drafts_black_24dp, "Drafts"),
                NavigationSubheaderViewModel("Subheader"),
                NavigationItemViewModel(R.drawable.ic_mail_black_24dp, "All mail"),
                NavigationItemViewModel(R.drawable.ic_delete_black_24dp, "Trash"),
                NavigationItemViewModel(R.drawable.ic_report_black_24dp, "Spam"))
    }

    fun itemSelected(view: View, model: NavigationItemViewModel) {
        Toast.makeText(view.context, "${model.title} selected!", Toast.LENGTH_SHORT).show()
    }

}