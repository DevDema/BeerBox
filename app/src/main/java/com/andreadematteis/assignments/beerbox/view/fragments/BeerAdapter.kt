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
import com.andreadematteis.assignments.beerbox.view.fragments.moreFilters.FilterType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BeerAdapter(
    private val context: Context,
    private val binder: BeerAdapterBinder,
    var dataSet: List<BeerAdapterItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var filterType: FilterType = FilterType.NONE
    private val filterName = object : Filter() {
        private var filteredValues = listOf<BeerAdapterItem>()

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            if (constraint.isNullOrBlank()) {
                filterType = FilterType.NONE
            }

            filteredValues = when (filterType) {
                FilterType.NAME -> performFilteringName(constraint!!)
                FilterType.TIMEFRAME -> performFilteringTimeframe(constraint!!)
                FilterType.NONE -> dataSet
            }

            return FilterResults().apply {
                values = filteredValues
            }
        }

        private fun performFilteringTimeframe(constraint: CharSequence): List<BeerAdapterItem> {
            val (startDateString, endDateString) = constraint
                .toString()
                .replace(" ", "")
                .split("-")

            val startDate = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).parse(startDateString)
            val endDate = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).parse(endDateString)

            return dataSet.filter { beerAdapterItem ->
                SimpleDateFormat("MM/yyyy", Locale.ITALY)
                    .parse(beerAdapterItem.beer.firstBrewed)
                    ?.let {
                        it.before(endDate) && it.after(startDate)
                    } ?: false
            }
        }

        private fun performFilteringName(constraint: CharSequence): List<BeerAdapterItem> =
            dataSet.filter {
                it.beer.name
                    .lowercase()
                    .contains(
                        constraint
                            .toString()
                            .lowercase()
                    )
            }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (filteredValues.isEmpty()) {
                CoroutineScope(Dispatchers.Main).launch { binder.onNoBeerResults() }
            } else {
                CoroutineScope(Dispatchers.Main).launch { binder.onBeerResults(filteredValues) }
            }
        }
    }

    var filteredDataset: MutableList<BeerAdapterItem> = dataSet.toMutableList()

    class ViewHolder(val itemBinding: ItemBeerBinding) : RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_beer,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindBeerViewHolder(holder as ViewHolder, position)

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

    override fun getItemCount() = filteredDataset.size

    override fun getFilter(): Filter = filterName

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