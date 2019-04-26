package com.example.domain.chat.usecase

import com.example.domain.chat.repository.ChatRepository
import com.example.domain.core.CompletableWithEmailCase

class SetUsersUseCase(private val repository: ChatRepository) : CompletableWithEmailCase<String> {
    override fun execute(t: String) = repository.setUsers(t)
}