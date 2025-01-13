package com.learninglab.swipeassignment.util

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.learninglab.swipeassignment.data.repository.ProductRepository
import java.util.concurrent.TimeUnit

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val repository: ProductRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            repository.syncUnsyncedProducts()
            // Show notification on successful sync
            SyncNotificationService(applicationContext).showSyncNotification(true)
            Result.success()
        } catch (e: Exception) {
            // Show notification on failed sync
            SyncNotificationService(applicationContext).showSyncNotification(false)
            Result.retry()
        }
    }

    companion object {
        fun startPeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
                15, TimeUnit.SECONDS,
                5, TimeUnit.SECONDS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    10, TimeUnit.SECONDS
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "product_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        }
    }
}