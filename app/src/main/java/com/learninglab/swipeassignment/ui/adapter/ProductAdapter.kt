package com.learninglab.swipeassignment.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.learninglab.swipeassignment.R
import com.learninglab.swipeassignment.data.model.Product
import com.learninglab.swipeassignment.databinding.ItemProductBinding

class ProductAdapter : ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {
    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(product: Product) {
            binding.apply {
                productNameText.text = product.productName
                productTypeText.text = product.productType
                priceText.text = "₹${product.price}"
                taxText.text = "${product.tax}%"

                // Load image with Glide
                Glide.with(productImage)
                    .load(product.image)
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .centerCrop()
                    .into(productImage)

                // Show sync indicator if product is not synced
                syncIndicator.isVisible = !product.isSynced
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
