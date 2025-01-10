package com.learninglab.swipeassignment.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productType: String,
    val productName: String,
    val price: Double,
    val tax: Double
)
