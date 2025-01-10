package com.learninglab.swipeassignment.service

import com.learninglab.swipeassignment.model.data.Product
import com.learninglab.swipeassignment.model.request.AddProductRequest
import com.learninglab.swipeassignment.model.response.AddProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @GET("public/get")
    suspend fun getProducts() : List<Product>

    @POST("public/add")
    suspend fun addProduct(@Part addProductRequest : AddProductRequest) : Response<AddProductResponse>
}