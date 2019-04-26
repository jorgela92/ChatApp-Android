package com.example.domain.auth.usecase

import com.example.domain.core.SingleEmailUseCase
import com.example.domain.auth.repository.AuthRepository

class EmailUseCase(private val repository: AuthRepository) : SingleEmailUseCase {

    override fun execute(): String? = repository.getUserAuth()

}