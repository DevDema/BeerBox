package com.andreadematteis.assignments.beerbox.view.fragments.moreFilters

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(): ViewModel() {

    val nameFilter = MutableLiveData<String>()
    val datesFilter = MutableLiveData<String>()


    fun forceClear() {
        this.nameFilter.value = ""
        this.datesFilter.value = ""
    }

    fun setFilterName(nameFilter: String) {
        this.nameFilter.value = nameFilter
    }
}
