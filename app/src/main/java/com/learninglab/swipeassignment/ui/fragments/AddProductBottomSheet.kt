package com.learninglab.swipeassignment.ui.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.learninglab.swipeassignment.R
import com.learninglab.swipeassignment.data.model.ApiResponse
import com.learninglab.swipeassignment.databinding.BottomSheetAddBinding
import com.learninglab.swipeassignment.ui.viewmodel.ProductListViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class AddProductBottomSheet : BottomSheetDialogFragment() {
    private val viewModel: ProductListViewModel by viewModel()
    private var _binding: BottomSheetAddBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.imageView.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }



    private fun setupViews() {
        binding.apply {
            // Setup product type spinner with proper adapter reference
            val spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.product_types,
                R.layout.simple_spinner_dropdown_item
            )
            productTypeSpinner.setAdapter(spinnerAdapter)


            imagePickerButton.setOnClickListener {
                getContent.launch("image/*")
            }

            submitButton.setOnClickListener {
                submitProduct()
            }
        }
    }

    private fun submitProduct() {
        binding.apply {
            val selectedType = productTypeSpinner.text.toString().trim()
            if (validateInputs()) {
                val file = selectedImageUri?.let { getFileFromUri(requireContext(), it) }
                if (file != null) {
                    viewModel.addProduct(
                        productNameInput.text.toString().trim(),
                        selectedType,
                        priceInput.text.toString().trim(),
                        taxInput.text.toString().trim(),
                        file.toUri()
                    )
                } else {
                    showError("Failed to process the selected image.")
                }
            }
        }
    } 


    // Fix 4: Add input validation
    private fun validateInputs(): Boolean {
        binding.apply {
            if (productNameInput.text.isNullOrBlank()) {
                showError("Product name is required")
                return false
            }
            if (priceInput.text.isNullOrBlank()) {
                showError("Price is required")
                return false
            }
            if (taxInput.text.isNullOrBlank()) {
                showError("Tax is required")
                return false
            }
            if (selectedImageUri == null) {
                showError("Please select an image")
                return false
            }
            return true
        }
    }


    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addProductResult.collect { response ->
                when (response) {
                    is ApiResponse.Loading -> showLoading()
                    is ApiResponse.Success -> {
                        hideLoading()
                        showSuccess(response.data.message)
                        dismiss()
                    }
                    is ApiResponse.Error -> {
                        hideLoading()
                        showError(response.message)
                        println(response.message)
                    }
                    null -> { /* Initial state, do nothing */ }
                }
            }
        }
    }
    private fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return null

            // Create a temporary file in the cache directory
            val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun showLoading() {
        binding.progressBar.isVisible = true
        binding.submitButton.isEnabled = false
    }

    private fun hideLoading() {
        binding.progressBar.isVisible = false
        binding.submitButton.isEnabled = true
    }

    private fun showSuccess(message: String) {
        showNotification(message)
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    private fun showError(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }

    private fun showNotification(message: String) {
        val channelId = "product_channel"
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Product Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(requireContext(), channelId)
            .setContentTitle("Product Added")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        setFragmentResult("", Bundle())
        _binding = null
    }
}