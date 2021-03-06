package com.andreadematteis.assignments.beerbox.network

import com.andreadematteis.assignments.beerbox.network.repositories.BeerRepository
import com.andreadematteis.assignments.beerbox.network.repositories.ImageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideBeerRepository(
        beerService: BeerService
    ): BeerRepository =
        BeerRepository(beerService)

    @Singleton
    @Provides
    fun provideImageRepository(
        imageService: ImageService
    ): ImageRepository =
        ImageRepository(imageService)
}