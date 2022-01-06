package com.satispay.assignment.beerbox

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satispay.assignment.beerbox.model.Beer
import com.satispay.assignment.beerbox.network.BeerRepository
import com.satispay.assignment.beerbox.network.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BeerViewModel @Inject constructor(
    private val beerRepository: BeerRepository,
    private val imageRepository: ImageRepository
) : ViewModel(), BeerAdapterBinder {

    var currentPage = 1
    lateinit var defaultImage: Bitmap
    private val beerList = mutableListOf<BeerAdapterItem>()
    private val beerAdapterItems = MutableLiveData<List<BeerAdapterItem>>()
    private val nothingMatchesVisibility = MutableLiveData<Boolean>()
    private val filteredBeerAdapterItems = MutableLiveData<List<BeerAdapterItem>>()
    private val internalToastMessage = MutableLiveData<CharSequence>()
    private val internalBeerDetailed = MutableLiveData<Pair<Beer?, Bitmap?>>()

    val beersAdapterItems: LiveData<List<BeerAdapterItem>>
        get() = beerAdapterItems
    val nothingMatchesVisibilityText: LiveData<Boolean>
        get() = nothingMatchesVisibility
    val filteredBeersAdapterItems: LiveData<List<BeerAdapterItem>>
        get() = filteredBeerAdapterItems
    val toastMessage: LiveData<CharSequence>
        get() = internalToastMessage
    val beerDetailed: LiveData<Pair<Beer?, Bitmap?>>
        get() = internalBeerDetailed

    fun loadBeers() {
        if (beersAdapterItems.value.isNullOrEmpty()) {
            loadPage(1)
        }
    }

    fun loadNextPage() {
        loadPage(++currentPage)
    }

    private fun loadPage(page: Int) {
        viewModelScope.launch {
            kotlin.runCatching {
                withContext(Dispatchers.IO) {
                    beerRepository.getBeers(page)
                }.map { BeerAdapterItem(it) }.let { list ->
                    beerList.addAll(list)

                    list.forEach {
                        loadImage(it.beer)
                    }

                    beerAdapterItems.value = list
                }
            }.exceptionOrNull()?.run {
                printStackTrace()
                internalToastMessage.value = "Error getting beer data: $message"
            }
        }
    }

    override fun loadImage(beer: Beer) {
        viewModelScope.launch {
            val beerList = withContext(Dispatchers.IO) {
                kotlin.runCatching {
                    val image = beer.imageUrl?.let {
                        BitmapFactory.decodeStream(
                            imageRepository.getImage(
                                it.replace(BuildConfig.IMAGES_BASE_URL, "")
                            ).byteStream()
                        )
                    } ?: defaultImage

                    beerList[beer.id - 1] = BeerAdapterItem(beer, false, image)
                }.exceptionOrNull()?.run {
                    Log.println(
                        Log.WARN, BeerViewModel::class.java.name,
                        "Error loading image for ${beer.name}: $this"
                    )

                    beerList[beer.id - 1] = BeerAdapterItem(beer, false)
                }

                beerList
            }

            beerAdapterItems.value = beerList
        }
    }

    override fun onNoBeerResults() {
        nothingMatchesVisibility.value = true
    }

    override fun onBeerResults(filteredValues: List<BeerAdapterItem>) {
        nothingMatchesVisibility.value = false
        filteredBeerAdapterItems.value = filteredValues
    }

    override fun showDetails(beer: Beer, bitmap: Bitmap?) {
        internalBeerDetailed.value = beer to bitmap
    }

    fun hideDetails() {
        internalBeerDetailed.value = null to null
    }
}