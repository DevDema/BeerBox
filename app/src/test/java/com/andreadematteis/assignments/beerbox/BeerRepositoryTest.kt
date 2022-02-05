package com.andreadematteis.assignments.beerbox

import com.andreadematteis.assignments.beerbox.model.Beer
import com.andreadematteis.assignments.beerbox.network.BeerService
import com.andreadematteis.assignments.beerbox.network.repositories.BeerRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class BeerRepositoryTest {

    private lateinit var beerRepository: BeerRepository

    @Before
    fun setup() {
        beerRepository = BeerRepository(mockk<BeerService>().apply {
            coEvery { getBeers(any()) } returns listOf(
                testBeer
            )
        })
    }

    @Test
    fun testGetBeers() {
        val beerList = runBlocking {
            beerRepository.getBeers(Random.nextInt(1, Int.MAX_VALUE))
        }

        assert(beerList.isNotEmpty())
        assert(beerList.size == 1)
    }

    @Test
    fun testGetBeersInvalidPage() {
        kotlin.runCatching {
            runBlocking {
                beerRepository.getBeers(Random.nextInt(Int.MIN_VALUE, 0))
            }
        }.exceptionOrNull()?.run {
            return
        }

        fail()
    }

    companion object {
        private val testBeer = Beer(
            "Your favourite beer description",
            "01/1970",
            0,
            "",
            "My Favourite beer",
            "Your favourite beer"
        )
    }
}