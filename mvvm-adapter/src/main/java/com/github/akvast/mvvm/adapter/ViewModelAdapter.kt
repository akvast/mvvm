package com.github.akvast.mvvm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.UiThread
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

abstract class ViewModelAdapter(
        lifecycleOwner: LifecycleOwner? = null)
    : RecyclerView.Adapter<ViewModelAdapter.ViewHolder>() {

    private val lifecycleOwnerRef = WeakReference(lifecycleOwner)

    private val cellMap = hashMapOf<Class<out Any>, CellInfo>()
    private val sharedObjects = hashMapOf<Int, Any>()

    protected var detectDiffMoves = false

    var items = arrayOf<Any>()
        @UiThread
        set(value) {
            try {
                val callback = DiffCallback(field, value)
                val diffResult = DiffUtil.calculateDiff(callback, detectDiffMoves)
                field = value
                diffResult.dispatchUpdatesTo(this)
            } catch (ex: NotImplementedError) {
                field = value
                notifyDataSetChanged()
            }
        }

    // Protected functions:

    protected fun cell(clazz: Class<out Any>, @LayoutRes layoutId: Int, bindingId: Int) {
        cellMap[clazz] = CellInfo(layoutId, bindingId)
    }

    protected fun sharedObject(sharedObject: Any, bindingId: Int) {
        sharedObjects[bindingId] = sharedObject
    }

    protected open fun getViewModel(position: Int) = items[position]

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

    // DiffUtil:

    protected open fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        throw NotImplementedError()
    }

    protected open fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return areItemsTheSame(oldItem, newItem)
    }

    protected open fun getChangePayload(oldItem: Any, newItem: Any): Any? {
        return null
    }

    private inner class DiffCallback(
            val oldItems: Array<Any>,
            val newItems: Array<Any>)
        : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]
            return this@ViewModelAdapter.areItemsTheSame(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]
            return this@ViewModelAdapter.areContentsTheSame(oldItem, newItem)
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]
            return this@ViewModelAdapter.getChangePayload(oldItem, newItem)
        }

    }

}