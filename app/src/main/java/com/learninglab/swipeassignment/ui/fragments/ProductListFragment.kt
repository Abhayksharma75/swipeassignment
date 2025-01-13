package com.learninglab.swipeassignment.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.learninglab.swipeassignment.R
import com.learninglab.swipeassignment.data.model.ApiResponse
import com.learninglab.swipeassignment.databinding.FragmentProductListBinding
import com.learninglab.swipeassignment.ui.adapter.CategoryAdapter
import com.learninglab.swipeassignment.ui.adapter.ProductAdapter
import com.learninglab.swipeassignment.ui.viewmodel.ProductListViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductListFragment : Fragment() {
    private val viewModel: ProductListViewModel by viewModel()
    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private val productAdapter = ProductAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeToRefresh()
        setupTouchListener()
        setupSearchView()
        setupAddButton()
        setupCategoryRecyclerView()
        observeFilteredProducts()
        observeViewModel()
        setFragmentResultListener("AddProduct") { _, _ ->
            refreshProductList()
        }
    }

    private fun refreshProductList() {
        println("Refreshing product list")
        viewModel.loadProducts()
        binding.swipeRefresh.isRefreshing = false
        productAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        val spanCount = 2 // Number of columns
        binding.productGrid.apply {
            adapter = productAdapter
            layoutManager = GridLayoutManager(context, spanCount)
        }
    }
    private class MarginItemDecoration(private val horizontalMargin: Int) :
        RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.left = horizontalMargin
            outRect.right = horizontalMargin
        }
    }

    private fun setupSearchView() {
        // Hide the hint when search is focused
        binding.searchView.setOnFocusChangeListener { _, hasFocus ->
            binding.searchLayout.hint = if (hasFocus) "" else getString(R.string.search_products)
        }

        binding.searchView.addTextChangedListener { text ->
            val query = text?.toString() ?: ""
            viewModel.searchProducts(query)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListener() {
        // Set up touch listener for parent view
        binding.root.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboardAndClearFocus(view)
            }
            false
        }

        // Set up touch listener for RecyclerView
        binding.productGrid.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboardAndClearFocus(view)
            }
            false
        }

        // Set up touch listener for CategoryRecyclerView
        binding.categoryRecyclerView.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboardAndClearFocus(view)
            }
            false
        }
    }


    private fun hideKeyboardAndClearFocus(view: View) {
        // Clear focus from search view
        binding.searchView.clearFocus()

        // Hide keyboard
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

        // Restore hint text
        binding.searchLayout.hint = getString(R.string.search_products)
    }

    private fun observeFilteredProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filteredProducts.collect { filteredProducts ->
                productAdapter.submitList(filteredProducts)
            }
        }
    }
    private fun setupSwipeToRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            refreshProductList()
        }

    }

    private fun setupAddButton() {
        binding.addButton.setOnClickListener {
            AddProductBottomSheet().show(childFragmentManager, "AddProduct")
        }
    }

    private fun setupCategoryRecyclerView() {
        // Set fixed size for better performance
        binding.categoryRecyclerView.setHasFixedSize(true)

        // Add item decoration for spacing
        binding.categoryRecyclerView.addItemDecoration(
            MarginItemDecoration(
                horizontalMargin = resources.getDimensionPixelSize(R.dimen.category_item_margin)
            )
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect { categories ->
                println("Updating categories: $categories")
                if (categories.isNotEmpty()) {
                    val categoryAdapter = CategoryAdapter(categories) { category ->
                        viewModel.filterProductsByCategory(category)
                        refreshCategories(category)
                    }
                    binding.categoryRecyclerView.adapter = categoryAdapter
                }
            }
        }
    }

    private fun refreshCategories(selectedCategory: String) {
        val updatedCategories = viewModel.categories.value.map {
            it.copy(isSelected = it.name == selectedCategory)
        }
        viewModel.updateCategories(updatedCategories)
        val selectedIndex = viewModel.categories.value.indexOfFirst { it.name == selectedCategory }
        if (selectedIndex != -1) {
            binding.categoryRecyclerView.smoothScrollToPosition(selectedIndex)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.collect { response ->
                when (response) {
                    is ApiResponse.Loading -> showLoading()
                    is ApiResponse.Success -> {
                        hideLoading()
                        productAdapter.submitList(response.data)
                    }
                    is ApiResponse.Error -> {
                        hideLoading()
                        showError(response.message)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.isVisible = true
    }

    private fun hideLoading() {
        binding.progressBar.isVisible = false
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
