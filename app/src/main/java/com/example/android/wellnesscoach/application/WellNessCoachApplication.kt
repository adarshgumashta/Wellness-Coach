package com.example.android.wellnesscoach.application

import android.app.Application
import com.example.android.wellnesscoach.di.repositoryModule
import com.example.android.wellnesscoach.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class WellNessCoachApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WellNessCoachApplication)
            modules(listOf(repositoryModule, viewModelModule))
        }
    }
}