package com.learninglab.swipeassignment.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.learninglab.swipeassignment.data.model.ProductEntity

@Database(entities = [ProductEntity::class], version = 1)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
