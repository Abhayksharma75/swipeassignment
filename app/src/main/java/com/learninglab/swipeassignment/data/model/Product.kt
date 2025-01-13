package com.learninglab.swipeassignment.data.model

import androidx.room.Ignore
import com.google.gson.annotations.SerializedName


data class Product(
    @SerializedName("product_name")
    val productName: String,

    @SerializedName("product_type")
    val productType: String,

    @SerializedName("price")
    val price: Double,

    @SerializedName("tax")
    val tax: Double,

    @SerializedName("image")
    val image: String?,

    // Local fields (not from API)
    @Ignore
    val id: Int = 0,

    @Ignore
    val isSynced: Boolean = true
)