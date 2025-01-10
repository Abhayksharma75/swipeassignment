package com.learninglab.swipeassignment.model.request

import okhttp3.MultipartBody
import java.io.File

data class AddProductRequest (
    val product_name : String,
    val product_type :String,
    val price: String,
    val tax: String,
    val files : List<MultipartBody.Part>?
)