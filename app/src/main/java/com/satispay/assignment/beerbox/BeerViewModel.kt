package com.satispay.assignment.beerbox

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.satispay.assignment.beerbox.model.Beer
import com.satispay.assignment.beerbox.network.BeerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class BeerViewModel @Inject constructor(
    private val beerRepository: BeerRepository
) : ViewModel() {

    private val beerList = MutableLiveData<List<Beer>>()
    private val internalToastMessage = MutableLiveData<CharSequence>()

    val beers: LiveData<List<Beer>>
        get() = beerList

    val toastMessage: LiveData<CharSequence>
        get() = internalToastMessage

    fun loadBeers() {
        viewModelScope.launch {
            kotlin.runCatching {
                beerList.value = beerRepository.getBeers()
            }.exceptionOrNull()?.run {
                printStackTrace()
                internalToastMessage.value = "Error getting beer data: $message"
            }
        }
    }
}