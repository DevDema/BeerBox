package com.andreadematteis.assignments.beerbox.view.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class FilterViewModel: ViewModel() {

    private val internalMoreFiltersOpened = MutableLiveData<Boolean>()

    val moreFiltersOpened: LiveData<Boolean>
        get() = internalMoreFiltersOpened

    fun setMoreFiltersOpened(moreFiltersOpened: Boolean) {
        this.internalMoreFiltersOpened.value = moreFiltersOpened
    }
}
