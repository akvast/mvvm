package com.github.akvast.mvvm.adapter

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.*
import kotlin.collections.LinkedHashMap

abstract class ViewModelAdapter : RecyclerView.Adapter<ViewModelAdapter.ViewHolder>() {

    protected val items = LinkedList<Any>()

    private val cellMap = LinkedHashMap<Class<out Any>, CellInfo>()
    private val sharedObjects = LinkedHashMap<Int, Any>()

    private var beginUpdateItemsSize = 0

    // Public functions:

    abstract fun reload(refreshLayout: SwipeRefreshLayout? = null)

    // Protected functions:

    protected fun cell(clazz: Class<out Any>, @LayoutRes layoutId: Int, bindingId: Int) {
        cellMap[clazz] = CellInfo(layoutId, bindingId)
    }

    protected fun sharedObject(sharedObject: Any, bindingId: Int) {
        sharedObjects[bindingId] = sharedObject
    }

    protected fun getViewModel(position: Int) = items[position]

    protected fun beginUpdates() {
        beginUpdateItemsSize = itemCount
    }

    protected fun endUpdates() {
        val changed = Math.min(beginUpdateItemsSize, itemCount)
        val diff = Math.max(beginUpdateItemsSize, itemCount) - changed

        if (diff == 0 && changed > 1) {
            notifyDataSetChanged()
            return
        }

        if (changed != 0) notifyItemRangeChanged(0, changed)

        if (diff > 0) {
            when (beginUpdateItemsSize > itemCount) {
                true -> notifyItemRangeRemoved(changed, diff)
                false -> notifyItemRangeInserted(changed, diff)
            }
        }
    }

    protected fun getCellInfo(viewModel: Any): CellInfo {
        // Find info with simple class check:
        cellMap.entries
                .find { it.key == viewModel.javaClass }
                ?.apply { return value }

        // Find info with inheritance class check:
        cellMap.entries
                .find { it.key.isInstance(viewModel) }
                ?.apply {
                    cellMap[viewModel.javaClass] = value
                    return value
                }

        throw Exception("Cell info for class ${viewModel.javaClass.name} not found.")
    }

    protected fun onBind(binding: ViewDataBinding,
                         cellInfo: CellInfo,
                         position: Int) {

        val viewModel = getViewModel(position)
        if (cellInfo.bindingId != 0)
            binding.setVariable(cellInfo.bindingId, viewModel)
    }

    // RecyclerView.Adapter

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return getCellInfo(getViewModel(position)).layoutId
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        val view = inflater.inflate(viewType, parent, false)
        val viewHolder = ViewHolder(view)

        sharedObjects.forEach { viewHolder.binding.setVariable(it.key, it.value) }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        if (holder != null) {
            val cellInfo = getCellInfo(getViewModel(position))
            onBind(holder.binding, cellInfo, position)
        }
    }

    // Support classes:

    data class CellInfo(val layoutId: Int, val bindingId: Int)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = DataBindingUtil.bind<ViewDataBinding>(view)
    }

}