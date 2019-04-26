package com.example.jorgelapenaanton.chatapp.viewmodel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.domain.chat.usecase.*
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class ChatViewModelFactory @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val setUsersUseCase: SetUsersUseCase,
    private val getConversationUseCase: GetConversationsUseCase,
    private val setConversationUseCase: SetConversationUseCase,
    private val getMessagesUseCase: GetMessageUseCase,
    private val setMessageUseCase: SetMessageUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            return ChatViewModel(getUsersUseCase, setUsersUseCase, getConversationUseCase, setConversationUseCase, getMessagesUseCase, setMessageUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}