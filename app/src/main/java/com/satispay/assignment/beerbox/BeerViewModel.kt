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

    private val beerList = MutableLiveData<List<Beer>>()
    private val internalToastMessage = MutableLiveData<CharSequence>()
    private val imageLoaded = MutableLiveData<Pair<Int, Bitmap?>>()
    private val internalBeerDetailed = MutableLiveData<Pair<Beer, Bitmap?>>()

    val beers: LiveData<List<Beer>>
        get() = beerList
    val toastMessage: LiveData<CharSequence>
        get() = internalToastMessage
    val beerImage: LiveData<Pair<Int, Bitmap?>>
        get() = imageLoaded
    val beerDetailed: LiveData<Pair<Beer, Bitmap?>>
        get() = internalBeerDetailed

    fun loadBeers() {
        viewModelScope.launch {
            kotlin.runCatching {
                val beers = withContext(Dispatchers.IO) {
                    beerRepository.getBeers()
                }

                beerList.value = beers
            }.exceptionOrNull()?.run {
                printStackTrace()
                internalToastMessage.value = "Error getting beer data: $message"
            }
        }
    }

    override fun loadImage(position: Int, urlString: String) {
        viewModelScope.launch {
            kotlin.runCatching {
                val image = withContext(Dispatchers.IO) {
                    BitmapFactory.decodeStream(
                        imageRepository.getImage(urlString).byteStream()
                    )
                }

                imageLoaded.value = position to image
            }.exceptionOrNull()?.run {
                Log.println(Log.WARN, BeerViewModel::class.java.name,
                    "Error loading image for position $position: $message"
                )
                imageLoaded.value = position to null
            }

        }
    }

    override fun showDetails(beer: Beer, bitmap: Bitmap?) {
        internalBeerDetailed.value = beer to bitmap
    }
}