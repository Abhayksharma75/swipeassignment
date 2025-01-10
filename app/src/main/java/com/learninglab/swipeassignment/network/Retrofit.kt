package com.learninglab.swipeassignment.network

import com.learninglab.swipeassignment.Constants
import com.learninglab.swipeassignment.service.ApiService
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit{

        private const val BASE_URL = Constants.BASE_URL

        val api: ApiService by lazy {
            retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
}

