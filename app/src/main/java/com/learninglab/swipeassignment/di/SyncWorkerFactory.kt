package com.learninglab.swipeassignment.di


import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.learninglab.swipeassignment.data.repository.ProductRepository
import com.learninglab.swipeassignment.util.SyncWorker
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncWorkerFactory : WorkerFactory(), KoinComponent {
    private val repository: ProductRepository by inject()

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SyncWorker::class.java.name ->
                SyncWorker(appContext, workerParameters, repository)
            else -> null
        }
    }
}