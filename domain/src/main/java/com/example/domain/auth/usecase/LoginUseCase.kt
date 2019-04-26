package com.example.domain.auth.usecase

import com.example.domain.core.SingleWithParamUseCase
import com.example.domain.auth.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) : SingleWithParamUseCase {

    override fun execute(username: String, password: String) = repository.login(username = username, password = password)
}