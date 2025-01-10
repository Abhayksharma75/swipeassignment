package com.learninglab.swipeassignment.model.response

import com.learninglab.swipeassignment.model.data.Product

data class AddProductResponse (
    val message: String,
    val product_details: Product,
    val product_id: Int,
    val success: Boolean

)