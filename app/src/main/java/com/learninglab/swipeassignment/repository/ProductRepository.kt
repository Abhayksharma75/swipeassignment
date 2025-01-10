package com.learninglab.swipeassignment.repository

import com.learninglab.swipeassignment.model.request.AddProductRequest
import com.learninglab.swipeassignment.service.ApiService

class ProductRepository (private val apiService: ApiService){

    suspend fun getProducts() = apiService.getProducts()

    suspend fun  addProducts(addProductRequest: AddProductRequest) = apiService.addProduct(addProductRequest)

}