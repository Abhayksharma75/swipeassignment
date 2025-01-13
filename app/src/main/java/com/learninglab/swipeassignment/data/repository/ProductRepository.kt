package com.learninglab.swipeassignment.data.repository

import android.net.Uri
import com.learninglab.swipeassignment.data.api.ProductApi
import com.learninglab.swipeassignment.data.local.ProductDao
import com.learninglab.swipeassignment.data.model.AddProductResponse
import com.learninglab.swipeassignment.data.model.ApiResponse
import com.learninglab.swipeassignment.data.model.Product
import com.learninglab.swipeassignment.data.model.ProductEntity
import com.learninglab.swipeassignment.data.model.ProductMapper.toDomain
import com.learninglab.swipeassignment.data.model.ProductMapper.toEntity
import com.learninglab.swipeassignment.util.NetworkHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ProductRepository(
    private val api: ProductApi,
    private val dao: ProductDao,
    private val networkHelper: NetworkHelper
) {
    fun getProducts(): Flow<ApiResponse<List<Product>>> = flow {
        emit(ApiResponse.Loading)
        try {
            if (networkHelper.isNetworkConnected()) {
                val response = api.getProducts()
                // Convert API response to entities and store in local DB
                val entities = response.map { it.toEntity() }
                dao.insertAll(entities)
                emit(ApiResponse.Success(response))
            } else {
                // Fetch from local DB and convert entities to domain models
                val localProducts = dao.getAllProducts().first().map { it.toDomain() }
                emit(ApiResponse.Success(localProducts))
            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.message ?: "Unknown error occurred"))
        }
    }



    suspend fun addProduct(
        productName: String,
        productType: String,
        price: Double,
        tax: Double,
        imageUri: Uri?
    ): Flow<ApiResponse<AddProductResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val product = Product(
                productName = productName,
                productType = productType,
                price = price,
                tax = tax,
                image = "",
                isSynced = networkHelper.isNetworkConnected(),
            )
            val productEntity = ProductEntity(
                productName = productName,
                productType = productType,
                price = price,
                tax = tax,
                image = "",
                isSynced = networkHelper.isNetworkConnected(),
            )

            if (networkHelper.isNetworkConnected()) {
                val response = api.addProduct(
                    productName.toRequestBody("text/plain".toMediaTypeOrNull()),
                    productType.toRequestBody("text/plain".toMediaTypeOrNull()),
                    price.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    tax.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    imageUri?.let { createImageMultipart(it) }
                )
                emit(ApiResponse.Success(response))
            } else {
                dao.insertProduct(productEntity)
                emit(ApiResponse.Success(AddProductResponse(
                    message = "Product saved locally",
                    productDetails = product,
                    productId = 0,
                    success = true
                )))

            }
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.message ?: "Unknown error occurred"))
        }
    }

    private fun createImageMultipart(uri: Uri): MultipartBody.Part {
        val file = File(uri.path!!)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("files[]", file.name, requestFile)
    }

    suspend fun syncUnsyncedProducts() {
        if (!networkHelper.isNetworkConnected()) return

        val unsyncedProducts = dao.getUnsyncedProducts()
        unsyncedProducts.forEach { product ->
            try {
                val response = api.addProduct(
                    product.productName.toRequestBody("text/plain".toMediaTypeOrNull()),
                    product.productType.toRequestBody("text/plain".toMediaTypeOrNull()),
                    product.price.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    product.tax.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    null
                )
                if (response.success) {
                    dao.markProductAsSynced(product.id)
                }
            } catch (e: Exception) {
                // Log error or handle failure
            }
        }
    }
}
