package com.capstone.tala

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TalaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Application-level initialization
    }
}