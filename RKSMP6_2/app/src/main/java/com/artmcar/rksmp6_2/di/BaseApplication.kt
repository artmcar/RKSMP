package com.artmcar.rksmp6_2.di

import android.app.Application


class BaseApplication : Application() {
    lateinit var container: ApplicationContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = ApplicationContainer()
    }
}