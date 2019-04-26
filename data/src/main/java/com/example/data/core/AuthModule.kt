package com.example.data.core

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AuthModule {

    @Provides
    @Singleton
        fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}