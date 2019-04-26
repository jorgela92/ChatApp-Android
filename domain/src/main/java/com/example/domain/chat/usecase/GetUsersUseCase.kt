package com.example.domain.chat.usecase

import com.example.domain.chat.model.Users
import com.example.domain.chat.repository.ChatRepository
import com.example.domain.core.ObservableUseCase
import io.reactivex.Observable

class GetUsersUseCase(private val repository: ChatRepository) : ObservableUseCase<Users> {

    override fun execute(): Observable<Users> = repository.updatetUsers()
}