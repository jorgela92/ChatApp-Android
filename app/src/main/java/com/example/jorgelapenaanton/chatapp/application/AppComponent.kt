package com.example.jorgelapenaanton.chatapp.application

import com.example.data.core.AuthModule
import com.google.firebase.firestore.FirebaseFirestore
import com.example.jorgelapenaanton.chatapp.viewmodel.auth.AuthViewModelFactory
import com.example.data.core.DatabaseModule
import com.example.data.core.RepositoryModule
import com.example.data.core.StorageModule
import com.example.domain.auth.repository.AuthRepository
import com.example.domain.chat.repository.ChatRepository
import com.example.domain.storage.repository.StorageRepository
import com.example.jorgelapenaanton.chatapp.viewmodel.chat.ChatViewModelFactory
import com.example.jorgelapenaanton.chatapp.viewmodel.storage.StorageViewModelFactory
import dagger.Component
import javax.inject.Singleton

@Component(modules = [
    RepositoryModule::class,
    DatabaseModule::class,
    ViewModelModule::class,
    AuthModule::class,
    StorageModule::class
])
@Singleton
interface AppComponent {

    fun authViewModelFactory(): AuthViewModelFactory

    fun chatViewModelFactory(): ChatViewModelFactory

    fun storageViewModelFactory(): StorageViewModelFactory

    fun authRepository(): AuthRepository

    fun chatRepository(): ChatRepository

    fun storageFirebase(): StorageRepository

    fun firebaseFirestore(): FirebaseFirestore

}