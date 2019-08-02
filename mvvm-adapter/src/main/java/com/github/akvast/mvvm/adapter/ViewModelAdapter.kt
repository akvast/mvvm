package com.github.akvast.mvvm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.UiThread
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference
import kotlin.math.max
import kotlin.math.min

abstract class ViewModelAdapter(
        lifecycleOwner: LifecycleOwner? = null)
    : RecyclerView.Adapter<ViewModelAdapter.ViewHolder>() {

    private val lifecycleOwnerRef = WeakReference(lifecycleOwner)

    private val cellMap = hashMapOf<Class<out Any>, CellInfo>()
    private val sharedObjects = hashMapOf<Int, Any>()

    private var beginUpdateItemsSize = 0

    var dynamicChanges = false

    var items = arrayOf<Any>()
        @UiThread
        set(value) {
            if (dynamicChanges) beginUpdates()
            field = value
            if (dynamicChanges) endUpdates()
            else notifyDataSetChanged()
        }

    // Protected functions:

    protected fun cell(clazz: Class<out Any>, @LayoutRes layoutId: Int, bindingId: Int) {
        cellMap[clazz] = CellInfo(layoutId, bindingId)
    }

    protected fun sharedObject(sharedObject: Any, bindingId: Int) {
        sharedObjects[bindingId] = sharedObject
    }

    protected open fun getViewModel(position: Int) = items[position]

    protected open fun beginUpdates() {
        beginUpdateItemsSize = itemCount
    }

    protected open fun endUpdates() {
        val changed = min(beginUpdateItemsSize, itemCount)
        val diff = max(beginUpdateItemsSize, itemCount) - changed

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

    protected open fun getCellInfo(viewModel: Any): CellInfo {
        // Find info with simple class check:
        cellMap[viewModel.javaClass]?.apply { return this }

        // Find info with inheritance class check:
        cellMap.entries
                .find { it.key.isInstance(viewModel) }
                ?.apply {
                    cellMap[viewModel.javaClass] = value
                    return value
                }

        throw Exception("Cell info for class ${viewModel.javaClass.name} not found.")
    }

    protected open fun onBind(binding: ViewDataBinding,
                              cellInfo: CellInfo,
                              position: Int) {

        val viewModel = getViewModel(position)
        if (cellInfo.bindingId != 0)
            binding.setVariable(cellInfo.bindingId, viewModel)

        binding.lifecycleOwner = lifecycleOwnerRef.get()
    }

    // RecyclerView.Adapter

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return getCellInfo(getViewModel(position)).layoutId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(viewType, parent, false)
        val viewHolder = ViewHolder(view)

        sharedObjects.forEach { viewHolder.binding.setVariable(it.key, it.value) }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cellInfo = getCellInfo(getViewModel(position))
        onBind(holder.binding, cellInfo, position)
    }

    // Support classes:

    data class CellInfo(val layoutId: Int, val bindingId: Int)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: ViewDataBinding = DataBindingUtil.bind(view)!!
    }

}