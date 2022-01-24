package com.andreadematteis.assignments.beerbox.view.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.andreadematteis.assignments.beerbox.R
import com.andreadematteis.assignments.beerbox.databinding.ItemBeerBinding
import com.andreadematteis.assignments.beerbox.databinding.ItemProgressBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BeerAdapter(
    private val context: Context,
    private val binder: BeerAdapterBinder,
    var dataSet: List<BeerAdapterItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var filteredDataset: MutableList<BeerAdapterItem> = dataSet.toMutableList()
    var isContentLoading = false
        private set

    class ViewHolder(val itemBinding: ItemBeerBinding) : RecyclerView.ViewHolder(itemBinding.root)
    class ViewHolderProgress(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            ITEM_TYPE_BEER -> ViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_beer,
                    parent,
                    false
                )
            )
            ITEM_TYPE_PROGRESS -> ViewHolderProgress(
                ItemProgressBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ).root
            )
            else -> error("Invalid view type")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ITEM_TYPE_BEER -> onBindBeerViewHolder(holder as ViewHolder, position)
            ITEM_TYPE_PROGRESS -> return
        }
    }

    private fun onBindBeerViewHolder(holder: ViewHolder, position: Int) {
        val beerAdapterItem = filteredDataset[position]
        holder.itemBinding.beer = beerAdapterItem.beer

        holder.itemBinding.reloadButton.setOnClickListener {
            showLoading(holder)
            binder.loadImage(position, beerAdapterItem.beer)
        }

        holder.itemBinding.moreInfoButton.setOnClickListener {
            binder.showDetails(beerAdapterItem.beer, beerAdapterItem.image)
        }

        holder.itemBinding.root.setOnClickListener {
            binder.showDetails(beerAdapterItem.beer, beerAdapterItem.image)
        }

        if (beerAdapterItem.loading) {
            showLoading(holder)
        } else {
            holder.itemBinding.progressCircular.visibility = View.INVISIBLE
            holder.itemBinding.beerImage.visibility = View.VISIBLE

            if (beerAdapterItem.image != null) {
                holder.itemBinding.reloadButton.visibility = View.INVISIBLE
                holder.itemBinding.beerImage.setBackgroundColor(context.getColor(android.R.color.transparent))
                holder.itemBinding.beerImage.setImageBitmap(beerAdapterItem.image)
            } else {
                holder.itemBinding.reloadButton.visibility = View.VISIBLE
                holder.itemBinding.beerImage.setBackgroundColor(context.getColor(android.R.color.tab_indicator_text))
                holder.itemBinding.beerImage.setImageBitmap(null)
            }
        }
    }

    private fun showLoading(holder: ViewHolder) {
        holder.itemBinding.progressCircular.visibility = View.VISIBLE
        holder.itemBinding.beerImage.visibility = View.INVISIBLE
        holder.itemBinding.reloadButton.visibility = View.GONE
    }

    fun showContentLoading() {
        isContentLoading = true

        if (filteredDataset.isNotEmpty()) {
            filteredDataset.add(filteredDataset.last())
        }

        notifyItemInserted(filteredDataset.size)
    }

    fun hideContentLoading() {
        isContentLoading = false

        if (filteredDataset.isNotEmpty()) {
            filteredDataset.removeLast()
        }

        notifyItemRemoved(filteredDataset.size)
    }

    override fun getItemViewType(position: Int): Int =
        if (position == itemCount - 1 && isContentLoading) {
            ITEM_TYPE_PROGRESS
        } else {
            ITEM_TYPE_BEER
        }

    override fun getItemCount() = filteredDataset.size

    companion object {
        const val ITEM_TYPE_PROGRESS = 1
        const val ITEM_TYPE_BEER = 0
    }

    override fun getFilter(): Filter =
        object : Filter() {
            private var filteredValues = listOf<BeerAdapterItem>()

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                filteredValues = if (constraint.isNullOrBlank()) {
                    dataSet
                } else {
                    dataSet.filter {
                        it.beer.name
                            .lowercase()
                            .contains(
                                constraint
                                    .toString()
                                    .lowercase()
                            )
                    }
                }

                return FilterResults().apply {
                    values = filteredValues
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (filteredValues.isEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch { binder.onNoBeerResults() }
                } else {
                    CoroutineScope(Dispatchers.Main).launch { binder.onBeerResults(filteredValues) }
                }
            }
        }

    fun onFiltered(list: List<BeerAdapterItem>) {
        val oldList = filteredDataset.toList()
        filteredDataset = list.toMutableList()

        if (oldList.size > filteredDataset.size) {
            notifyItemRangeRemoved(filteredDataset.size, oldList.size)

        } else if (oldList.size < filteredDataset.size) {
            notifyItemRangeInserted(oldList.size, filteredDataset.size)

        }

        filteredDataset.zip(oldList).forEach {
            if (it.first != it.second) {
                notifyItemChanged(filteredDataset.indexOf(it.first))
            }
        }
    }
}