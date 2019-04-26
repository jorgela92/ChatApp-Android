package com.example.domain.auth.usecase

import com.example.domain.core.SingleWithParamUseCase
import com.example.domain.auth.repository.AuthRepository

class SignUpUseCase(private val repository: AuthRepository) : SingleWithParamUseCase {

    override fun execute(username: String, password: String ) = repository.signup(username = username, password = password)
}