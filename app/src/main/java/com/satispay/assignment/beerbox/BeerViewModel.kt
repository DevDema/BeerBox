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

    private val beerList = MutableLiveData<BeerAdapterItem>()
    private val internalToastMessage = MutableLiveData<CharSequence>()
    private val internalBeerDetailed = MutableLiveData<Pair<Beer, Bitmap?>>()

    val beersAdapterItems: LiveData<BeerAdapterItem>
        get() = beerList
    val toastMessage: LiveData<CharSequence>
        get() = internalToastMessage
    val beerDetailed: LiveData<Pair<Beer, Bitmap?>>
        get() = internalBeerDetailed

    fun loadBeers() {
        viewModelScope.launch {
            kotlin.runCatching {
                withContext(Dispatchers.IO) {
                    beerRepository.getBeers()
                }.forEach {
                    beerList.value = BeerAdapterItem(it)
                    loadImage(it)
                }

            }.exceptionOrNull()?.run {
                printStackTrace()
                internalToastMessage.value = "Error getting beer data: $message"
            }
        }
    }

    override fun loadImage(beer: Beer) {
        viewModelScope.launch {
            kotlin.runCatching {
                val image = withContext(Dispatchers.IO) {
                    BitmapFactory.decodeStream(
                        imageRepository.getImage(
                            beer.imageUrl
                                .replace(BuildConfig.IMAGES_BASE_URL, "")
                        )
                            .byteStream()
                    )
                }

                beerList.value = BeerAdapterItem(beer, false, image)
            }.exceptionOrNull()?.run {
                Log.println(
                    Log.WARN, BeerViewModel::class.java.name,
                    "Error loading image for ${beer.name}: $message"
                )

                beerList.value = BeerAdapterItem(beer, false)
            }
        }
    }

    override fun showDetails(beer: Beer, bitmap: Bitmap?) {
        internalBeerDetailed.value = beer to bitmap
    }
}