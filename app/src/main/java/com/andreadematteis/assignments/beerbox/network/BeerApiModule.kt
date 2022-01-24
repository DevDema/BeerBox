package com.andreadematteis.assignments.beerbox.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.andreadematteis.assignments.beerbox.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BeerApiModule {

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson =
        GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()

    @Singleton
    @Provides
    @Named("beers")
    fun provideRetrofitBeers(gson: Gson): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BEERS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))

    @Singleton
    @Provides
    fun provideBeerService(@Named("beers") retrofit: Retrofit.Builder): BeerService =
        retrofit
            .build()
            .create(BeerService::class.java)
}