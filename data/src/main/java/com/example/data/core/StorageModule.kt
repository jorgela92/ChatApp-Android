package com.example.data.core

import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StorageModule {

    @Provides
    @Singleton
    fun providesFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance("gs://wathasapp-9dc3a.appspot.com/")
}