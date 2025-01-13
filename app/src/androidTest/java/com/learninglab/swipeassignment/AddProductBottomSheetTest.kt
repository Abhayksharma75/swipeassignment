package com.learninglab.swipeassignment

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.learninglab.swipeassignment.ui.fragments.AddProductBottomSheet
import com.learninglab.swipeassignment.ui.viewmodel.ProductListViewModel
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
class AddProductBottomSheetTest {
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
    fun testValidationBehavior() {
        // Given
        val scenario = launchFragmentInContainer<AddProductBottomSheet>()

        // When - Submit with empty fields
        onView(withId(R.id.submitButton)).perform(click())

        // Then
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("Product name is required")))

        // When - Fill product name only
        onView(withId(R.id.productNameInput))
            .perform(typeText("Test Product"), closeSoftKeyboard())
        onView(withId(R.id.submitButton)).perform(click())

        // Then
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("Price is required")))
    }
}