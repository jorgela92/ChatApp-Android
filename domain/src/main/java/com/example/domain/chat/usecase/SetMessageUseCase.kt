package com.example.domain.chat.usecase

import com.example.domain.chat.repository.ChatRepository
import com.example.domain.core.CompletableWithMessageCase


class SetMessageUseCase(private val repository: ChatRepository) : CompletableWithMessageCase {
    override fun execute(identifier: String, user: String, t: String) = repository.setMessage(identifier, user,  t)
}