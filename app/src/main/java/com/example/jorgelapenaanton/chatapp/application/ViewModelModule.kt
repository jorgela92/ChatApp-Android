package com.example.jorgelapenaanton.chatapp.application

import com.example.data.repository.AuthRepositoryImpl
import com.example.data.repository.ChatRepositoryImpl
import com.example.data.repository.StorageRepositoryImpl
import com.example.domain.auth.usecase.EmailUseCase
import com.example.domain.auth.usecase.LogautUseCase
import com.example.domain.auth.usecase.LoginUseCase
import com.example.domain.auth.usecase.SignUpUseCase
import com.example.domain.chat.usecase.*
import com.example.domain.storage.usecase.GetImageUseCase
import com.example.domain.storage.usecase.SetImageUseCase
import com.example.jorgelapenaanton.chatapp.viewmodel.auth.AuthViewModelFactory
import com.example.jorgelapenaanton.chatapp.viewmodel.chat.ChatViewModelFactory
import com.example.jorgelapenaanton.chatapp.viewmodel.storage.StorageViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class ViewModelModule {

    @Provides
    fun providesAuthViewModelFactory(repository: AuthRepositoryImpl): AuthViewModelFactory {
        return AuthViewModelFactory(
            SignUpUseCase(repository),
            LoginUseCase(repository),
            LogautUseCase(repository),
            EmailUseCase(repository)
        )
    }

    @Provides
    fun providesChatViewModelFactory(repository: ChatRepositoryImpl): ChatViewModelFactory {
        return ChatViewModelFactory(
                GetUsersUseCase(repository),
                SetUsersUseCase(repository),
                GetConversationsUseCase(repository),
                SetConversationUseCase(repository),
                GetMessageUseCase(repository),
                SetMessageUseCase(repository)
        )
    }

    @Provides
    fun providesStorageViewModelFactory(repository: StorageRepositoryImpl): StorageViewModelFactory {
        return StorageViewModelFactory(
            GetImageUseCase(repository),
            SetImageUseCase(repository)
        )
    }
}