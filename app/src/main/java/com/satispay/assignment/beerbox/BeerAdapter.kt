package com.satispay.assignment.beerbox

import android.content.Context
import android.graphics.Bitmap
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
    private val dataSet: List<Beer>
) : RecyclerView.Adapter<BeerAdapter.ViewHolder>() {

    private val bitmapMap = mutableMapOf<Int, Bitmap?>()
    private val loadingMap = mutableMapOf<Int, Boolean>()

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
        val beer = dataSet[position]
        holder.itemBinding.beer = beer

        holder.itemBinding.reloadButton.setOnClickListener {
            startLoadingImage(holder, position, beer.imageUrl)
        }

        holder.itemBinding.moreInfoButton.setOnClickListener {
            binder.showDetails(beer, bitmapMap[position])
        }

        if (loadingMap.getOrDefault(position, true)) {
            startLoadingImage(holder, position, beer.imageUrl)
        } else {
            holder.itemBinding.progressCircular.visibility = View.INVISIBLE
            holder.itemBinding.beerImage.visibility = View.VISIBLE

            val image = bitmapMap[position]

            if (image != null) {
                holder.itemBinding.beerImage.setBackgroundColor(context.getColor(android.R.color.transparent))
                holder.itemBinding.beerImage.setImageBitmap(image)
            } else {
                holder.itemBinding.reloadButton.visibility = View.VISIBLE
                holder.itemBinding.beerImage.setBackgroundColor(context.getColor(android.R.color.tab_indicator_text))
            }
        }
    }

    private fun startLoadingImage(holder: ViewHolder, position: Int, beerUrl: String) {
        holder.itemBinding.progressCircular.visibility = View.VISIBLE
        holder.itemBinding.beerImage.visibility = View.INVISIBLE
        holder.itemBinding.reloadButton.visibility = View.GONE

        binder.loadImage(position, beerUrl.replace(BuildConfig.IMAGES_BASE_URL, ""))
    }

    fun onImageLoaded(position: Int, image: Bitmap?) {
        bitmapMap[position] = image
        loadingMap[position] = false
        notifyItemChanged(position)
    }

    override fun getItemCount() = dataSet.size
}