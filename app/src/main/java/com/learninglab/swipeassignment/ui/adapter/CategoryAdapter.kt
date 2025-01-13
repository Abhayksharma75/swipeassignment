package com.learninglab.swipeassignment.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.learninglab.swipeassignment.R
import com.learninglab.swipeassignment.data.model.Category
import com.learninglab.swipeassignment.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.categoryName.text = category.name

            // Update the card's checked state
            (binding.root as MaterialCardView).apply {
                isChecked = category.isSelected
                setCardBackgroundColor(if (category.isSelected)
                    context.getColor(R.color.primary_color)
                else context.getColor(R.color.surface_color))
                binding.categoryName.setTextColor(if (category.isSelected)
                    context.getColor(android.R.color.white)
                else context.getColor(R.color.primary_text))
            }

            binding.root.setOnClickListener {
                onCategoryClick(category.name)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size
}