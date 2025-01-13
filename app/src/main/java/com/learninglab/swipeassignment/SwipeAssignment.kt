package com.learninglab.swipeassignment

import android.app.Application
import androidx.work.Configuration
import com.learninglab.swipeassignment.di.SyncWorkerFactory
import com.learninglab.swipeassignment.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SwipeAssignment : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SwipeAssignment)
            modules(appModule)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(SyncWorkerFactory())
            .build()
}
