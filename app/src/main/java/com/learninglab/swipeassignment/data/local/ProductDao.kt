package com.learninglab.swipeassignment.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.learninglab.swipeassignment.data.model.Product
import com.learninglab.swipeassignment.data.model.ProductEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Query("SELECT * FROM products WHERE isSynced = 0")
    suspend fun getUnsyncedProducts(): List<ProductEntity>

    @Query("UPDATE products SET isSynced = 1 WHERE id = :productId")
    suspend fun markProductAsSynced(productId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)
}
