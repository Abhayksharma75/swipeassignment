package com.learninglab.swipeassignment.di

import androidx.room.Room
import com.learninglab.swipeassignment.Constants
import com.learninglab.swipeassignment.data.api.ProductApi
import com.learninglab.swipeassignment.data.local.ProductDatabase
import com.learninglab.swipeassignment.data.repository.ProductRepository
import com.learninglab.swipeassignment.ui.viewmodel.ProductListViewModel
import com.learninglab.swipeassignment.util.NetworkHelper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    val baseUrl = Constants.BASE_URL
    // Network
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(ProductApi::class.java) }

    // Database
    single {
        Room.databaseBuilder(
            get(),
            ProductDatabase::class.java,
            "product_database"
        ).build()
    }

    single { get<ProductDatabase>().productDao() }

    // Repository
    single { NetworkHelper(get()) }
    single { ProductRepository(get(), get(), get()) }

    // ViewModels
    viewModel { ProductListViewModel(get()) }
}