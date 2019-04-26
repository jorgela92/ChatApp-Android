package com.example.data.core

import com.example.data.repository.AuthRepositoryImpl
import com.example.data.repository.ChatRepositoryImpl
import com.example.data.repository.StorageRepositoryImpl
import com.example.domain.chat.repository.ChatRepository
import com.example.domain.auth.repository.AuthRepository
import com.example.domain.storage.repository.StorageRepository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {

    @Provides
    fun providesAuthRepository(repository: AuthRepositoryImpl): AuthRepository {
        return repository
    }

    @Provides
    fun providesChatRepository(repository: ChatRepositoryImpl): ChatRepository {
        return repository
    }

    @Provides
    fun providesStorageRepository(repository: StorageRepositoryImpl): StorageRepository {
        return repository
    }
}