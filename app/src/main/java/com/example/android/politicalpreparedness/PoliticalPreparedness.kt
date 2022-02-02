package com.example.android.politicalpreparedness

import android.app.Application
import timber.log.Timber

class PoliticalPreparedness : Application() {

    lateinit var appContainer : AppContainer

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        appContainer = AppContainer(applicationContext)
    }
}