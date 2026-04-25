package com.artmcar.rksmp5_4

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile

class TodoApplication : Application() {

    lateinit var dataStore: DataStore<Preferences>

    override fun onCreate() {
        super.onCreate()

        dataStore = PreferenceDataStoreFactory.create {
            preferencesDataStoreFile("settings")
        }
    }
}