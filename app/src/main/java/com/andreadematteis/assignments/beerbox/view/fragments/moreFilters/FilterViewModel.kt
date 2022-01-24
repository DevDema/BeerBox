package com.andreadematteis.assignments.beerbox.view.fragments.moreFilters

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class FilterViewModel: ViewModel() {

    val nameFilter = MutableLiveData<String>()
    val datesFilter = MutableLiveData<String>()
}
