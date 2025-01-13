package com.learninglab.swipeassignment.data.model

// Mapper to convert between Entity and Domain models
object ProductMapper {
    fun Product.toEntity() = ProductEntity(
        productName = productName,
        productType = productType,
        price = price,
        tax = tax,
        image = image,
        isSynced = isSynced
    )

    fun ProductEntity.toDomain() = Product(
        id = id,
        productName = productName,
        productType = productType,
        price = price,
        tax = tax,
        image = image,
        isSynced = isSynced
    )
}