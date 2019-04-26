package com.example.jorgelapenaanton.chatapp.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.domain.auth.usecase.EmailUseCase
import com.example.domain.auth.usecase.LogautUseCase
import com.example.domain.auth.usecase.LoginUseCase
import com.example.domain.auth.usecase.SignUpUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class AuthViewModelFactory @Inject constructor(
    private val signUp: SignUpUseCase,
    private val login: LoginUseCase,
    private val logaut: LogautUseCase,
    private val email: EmailUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(signUp, login, logaut, email) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}