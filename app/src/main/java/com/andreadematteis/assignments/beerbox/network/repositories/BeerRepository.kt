package com.andreadematteis.assignments.beerbox.network.repositories

import com.andreadematteis.assignments.beerbox.model.Beer
import com.andreadematteis.assignments.beerbox.network.BeerService
import com.andreadematteis.assignments.beerbox.utils.OpenForTesting

@OpenForTesting
class BeerRepository(
    private val service: BeerService
) {

    suspend fun getBeers(page: Int): List<Beer> {
        if(page < 1) {
            error("Invalid page $page. Should be >= 1.")
        }

        return service.getBeers(page)
    }
}