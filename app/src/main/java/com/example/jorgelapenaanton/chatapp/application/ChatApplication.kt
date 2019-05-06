package com.example.jorgelapenaanton.chatapp.application

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.google.firebase.FirebaseApp
import com.example.data.core.DatabaseModule
import com.example.data.core.RepositoryModule
import com.google.firebase.analytics.FirebaseAnalytics
import io.fabric.sdk.android.Fabric
import io.reactivex.plugins.RxJavaPlugins

@Suppress("unused")
class ChatApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(baseContext)
        Fabric.with(this, Crashlytics())
        FirebaseAnalytics.getInstance(this)
        injector = DaggerAppComponent.builder()
            .databaseModule(DatabaseModule())
            .repositoryModule(RepositoryModule())
            .viewModelModule(ViewModelModule())
            .build()
        RxJavaPlugins.setErrorHandler { print(it.localizedMessage) }
    }
}

lateinit var injector: AppComponent