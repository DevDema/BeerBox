package com.satispay.assignment.beerbox.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.satispay.assignment.beerbox.BuildConfig
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
object ImageApiModule {

    @Singleton
    @Provides
    @Named("images")
    fun provideRetrofitImages(): Retrofit.Builder =
        Retrofit.Builder()
            .baseUrl(BuildConfig.IMAGES_BASE_URL)

    @Singleton
    @Provides
    fun provideImageService(@Named("images") retrofit: Retrofit.Builder): ImageService =
        retrofit
            .build()
            .create(ImageService::class.java)
}