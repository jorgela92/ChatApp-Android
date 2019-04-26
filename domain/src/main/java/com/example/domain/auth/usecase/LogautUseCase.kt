package com.example.domain.auth.usecase

import com.example.domain.core.SingleUseCase
import com.example.domain.auth.repository.AuthRepository

class LogautUseCase(private val repository: AuthRepository) : SingleUseCase {
    override fun execute() = repository.logautUser()
}
