package com.learninglab.swipeassignment

import android.net.Uri
import com.learninglab.swipeassignment.data.model.AddProductResponse
import com.learninglab.swipeassignment.data.model.ApiResponse
import com.learninglab.swipeassignment.data.model.Category
import com.learninglab.swipeassignment.data.model.Product
import com.learninglab.swipeassignment.data.repository.ProductRepository
import com.learninglab.swipeassignment.ui.viewmodel.ProductListViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ProductListViewModelTest {
    private lateinit var viewModel: ProductListViewModel
    private lateinit var repository: ProductRepository
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        viewModel = ProductListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `loadProducts updates categories and filtered products on success`() = runBlockingTest {
        // Given
        val products = listOf(
            Product( "Product1", "Type1", 100.0, 10.0, "url1"),
            Product("Product2", "Type2", 200.0, 20.0, "url2")
        )
        coEvery { repository.getProducts() } returns flowOf(ApiResponse.Success(products))

        // When
        viewModel.loadProducts()

        // Then
        assertEquals(3, viewModel.categories.value.size) // All + Type1 + Type2
        assertEquals("All", viewModel.categories.value[0].name)
        assertEquals(products, viewModel.filteredProducts.value)
    }

    @Test
    fun `searchProducts filters products correctly`() = runBlockingTest {
        // Given
        val products = listOf(
            Product("Apple", "Fruit", 100.0, 10.0, "url1"),
            Product("Banana", "Fruit", 200.0, 20.0, "url2")
        )
        coEvery { repository.getProducts() } returns flowOf(ApiResponse.Success(products))
        viewModel.loadProducts()

        // When
        viewModel.searchProducts("app")

        // Then
        assertEquals(1, viewModel.filteredProducts.value.size)
        assertEquals("Apple", viewModel.filteredProducts.value[0].productName)
    }

    @Test
    fun `filterProductsByCategory filters products correctly`() = runBlockingTest {
        // Given
        val products = listOf(
            Product("Apple", "Fruit", 100.0, 10.0, "url1"),
            Product("Carrot", "Vegetable", 200.0, 20.0, "url2")
        )
        coEvery { repository.getProducts() } returns flowOf(ApiResponse.Success(products))
        viewModel.loadProducts()

        // When
        viewModel.filterProductsByCategory("Fruit")

        // Then
        assertEquals(1, viewModel.filteredProducts.value.size)
        assertEquals("Apple", viewModel.filteredProducts.value[0].productName)
    }

    @Test
    fun `addProduct validates input correctly`() = runBlockingTest {
        // Given
        val products = Product( "Product1", "Type1", 100.0, 10.0, "url1")
        val response = AddProductResponse("Success", products, 1, true)
        coEvery {
            repository.addProduct(any(), any(), any(), any(), any())
        } returns flowOf(ApiResponse.Success(response))

        // When - Valid Input
        viewModel.addProduct("Test Product", "Type", "100.0", "10.0", mockk())

        // Then
        assertEquals(ApiResponse.Success(response), viewModel.addProductResult.value)

        // When - Invalid Input
        viewModel.addProduct("", "", "invalid", "invalid", mockk())

        // Then
        assertEquals(ApiResponse.Error("Invalid input"), viewModel.addProductResult.value)
    }
}