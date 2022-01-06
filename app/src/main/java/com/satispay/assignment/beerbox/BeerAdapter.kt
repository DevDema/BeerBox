package com.satispay.assignment.beerbox

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.satispay.assignment.beerbox.databinding.ItemBeerBinding
import com.satispay.assignment.beerbox.model.Beer

class BeerAdapter(
    private val context: Context,
    private val binder: BeerAdapterBinder,
    val dataSet: MutableList<BeerAdapterItem>
) : RecyclerView.Adapter<BeerAdapter.ViewHolder>() {

    class ViewHolder(val itemBinding: ItemBeerBinding) : RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_beer,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val beerAdapterItem = dataSet[position]
        holder.itemBinding.beer = beerAdapterItem.beer

        holder.itemBinding.reloadButton.setOnClickListener {
            showLoading(holder)
            binder.loadImage(beerAdapterItem.beer)
        }

        holder.itemBinding.moreInfoButton.setOnClickListener {
            binder.showDetails(beerAdapterItem.beer, beerAdapterItem.image)
        }

        if (beerAdapterItem.loading) {
            showLoading(holder)
        } else {
            holder.itemBinding.progressCircular.visibility = View.INVISIBLE
            holder.itemBinding.beerImage.visibility = View.VISIBLE

            if (beerAdapterItem.image != null) {
                holder.itemBinding.beerImage.setBackgroundColor(context.getColor(android.R.color.transparent))
                holder.itemBinding.beerImage.setImageBitmap(beerAdapterItem.image)
            } else {
                holder.itemBinding.reloadButton.visibility = View.VISIBLE
                holder.itemBinding.beerImage.setBackgroundColor(context.getColor(android.R.color.tab_indicator_text))
            }
        }
    }

    private fun showLoading(holder: ViewHolder) {
        holder.itemBinding.progressCircular.visibility = View.VISIBLE
        holder.itemBinding.beerImage.visibility = View.INVISIBLE
        holder.itemBinding.reloadButton.visibility = View.GONE
    }

    override fun getItemCount() = dataSet.size
}