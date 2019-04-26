package com.example.jorgelapenaanton.chatapp.viewmodel.auth

import androidx.lifecycle.ViewModel
import com.example.domain.auth.usecase.EmailUseCase
import com.example.domain.auth.usecase.LogautUseCase
import com.example.domain.auth.usecase.LoginUseCase
import com.example.domain.auth.usecase.SignUpUseCase

class AuthViewModel(private val signUp: SignUpUseCase, private val login: LoginUseCase, private val logaut: LogautUseCase, private val email: EmailUseCase) : ViewModel() {

    fun signUpUser(username: String, password: String) = signUp.execute(username = username, password = password)

    fun loginUser(username: String, password: String) = login.execute(username = username, password = password)

    fun logautUser() = logaut.execute()

    fun emailUser(): String? = email.execute()
}