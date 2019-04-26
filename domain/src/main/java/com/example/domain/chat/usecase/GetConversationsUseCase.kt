package com.example.domain.chat.usecase

import com.example.domain.chat.repository.ChatRepository
import com.example.domain.core.ObservableUseCase
import io.reactivex.Observable

class GetConversationsUseCase(private val repository: ChatRepository) : ObservableUseCase<Array<String>> {

    override fun execute(): Observable<Array<String>> = repository.getConversations()
}