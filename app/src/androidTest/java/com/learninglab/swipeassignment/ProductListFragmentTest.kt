package com.learninglab.swipeassignment

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.learninglab.swipeassignment.data.model.ApiResponse
import com.learninglab.swipeassignment.data.model.Product
import com.learninglab.swipeassignment.ui.fragments.ProductListFragment
import com.learninglab.swipeassignment.ui.viewmodel.ProductListViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Test
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class ProductListFragmentTest {
    private lateinit var viewModel: ProductListViewModel

    @Before
    fun setup() {
        viewModel = mockk(relaxed = true)

        startKoin {
            modules(module {
                viewModel { viewModel }
            })
        }
    }

    @Test
    fun testSearchViewBehavior() {
        // Given
        val products = listOf(
            Product("Test Product", "Type", 100.0, 10.0, "url")
        )
        val productsFlow = MutableStateFlow<ApiResponse<List<Product>>>(ApiResponse.Success(products))
        coEvery { viewModel.products } returns productsFlow

        // When
        val fragmentArgs = bundleOf("key" to "value")
        launchFragmentInContainer<ProductListFragment>(fragmentArgs)

        // Then
        onView(withId(R.id.searchView))
            .perform(click())
            .check(matches(hasFocus()))

        onView(withId(R.id.searchLayout))
            .check(matches(withHint("Search here")))

        onView(withId(R.id.root))
            .perform(click())

        onView(withId(R.id.searchView))
            .check(matches(not(hasFocus())))
    }
}
