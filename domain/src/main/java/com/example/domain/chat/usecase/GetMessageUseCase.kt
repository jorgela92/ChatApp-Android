package com.example.domain.chat.usecase

import com.example.domain.chat.model.Message
import com.example.domain.chat.repository.ChatRepository
import com.example.domain.core.ObservableMessageUseCase
import io.reactivex.Observable

class GetMessageUseCase(private val repository: ChatRepository) : ObservableMessageUseCase<Array<Message>> {
    override fun execute(identifier: String): Observable<Array<Message>> = repository.updateMessage(identifier)
}