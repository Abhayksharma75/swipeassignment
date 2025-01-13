package com.learninglab.swipeassignment.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learninglab.swipeassignment.data.model.AddProductResponse
import com.learninglab.swipeassignment.data.model.ApiResponse
import com.learninglab.swipeassignment.data.model.Category
import com.learninglab.swipeassignment.data.model.Product
import com.learninglab.swipeassignment.data.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val repository: ProductRepository
) : ViewModel() {
    private val _products = MutableStateFlow<ApiResponse<List<Product>>>(ApiResponse.Loading)
    val products: StateFlow<ApiResponse<List<Product>>> = _products.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts.asStateFlow()

    private val _addProductResult = MutableStateFlow<ApiResponse<AddProductResponse>?>(null)
    val addProductResult: StateFlow<ApiResponse<AddProductResponse>?> = _addProductResult.asStateFlow()

    init {
        loadProducts()

    }

    fun loadProducts() {
        viewModelScope.launch {
            repository.getProducts().collect { it ->
                _products.value = it
                if (it is ApiResponse.Success) {
                    val types = it.data.map { it.productType }.distinct()
                    val categoryList = mutableListOf(Category("All", true))
                    categoryList.addAll(types.map { Category(it) })
                    _categories.value = categoryList
                    _filteredProducts.value = it.data
                }
            }
        }
    }


    fun addProduct(
        productName: String,
        productType: String,
        price: String,
        tax: String,
        imageUri: Uri?
    ) {
        if (!validateInput(productName, productType, price, tax)) {
            _addProductResult.value = ApiResponse.Error("Invalid input")
            return
        }

        viewModelScope.launch {
            repository.addProduct(
                productName,
                productType,
                price.toDouble(),
                tax.toDouble(),
                imageUri
            ).collect {
                _addProductResult.value = it
            }
        }
        loadProducts()
    }

    private fun validateInput(
        productName: String,
        productType: String,
        price: String,
        tax: String
    ): Boolean {
        return productName.isNotBlank() &&
                productType.isNotBlank() &&
                price.toDoubleOrNull() != null &&
                tax.toDoubleOrNull() != null
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        getFilteredProducts()
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            products.collect { response ->
                when (response) {
                    is ApiResponse.Success -> {
                        val filtered = if (query.isEmpty()) {
                            response.data
                        } else {
                            response.data.filter { product ->
                                product.productName.lowercase().contains(query.lowercase())
                            }
                        }
                        _filteredProducts.value = filtered
                    }
                    else -> {
                        // Handle other states
                    }
                }
            }
        }
    }

    fun getFilteredProducts(): Flow<List<Product>> = products.map { response ->
        when (response) {
            is ApiResponse.Success -> {
                response.data.filter {
                    it.productName.contains(searchQuery.value, ignoreCase = true)

                }
            }
            else -> emptyList()
        }

    }
    fun filterProductsByCategory(category: String) {
        val allProducts = (products.value as? ApiResponse.Success)?.data ?: emptyList()
        _filteredProducts.value = if (category == "All") {
            allProducts
        } else {
            allProducts.filter { it.productType == category }
        }
    }

    fun updateCategories(updatedCategories: List<Category>) {
        _categories.value = updatedCategories
        // Update filtered products based on selected category
        val selectedCategory = updatedCategories.find { it.isSelected }?.name ?: "All"
        filterProductsByCategory(selectedCategory)
    }
}