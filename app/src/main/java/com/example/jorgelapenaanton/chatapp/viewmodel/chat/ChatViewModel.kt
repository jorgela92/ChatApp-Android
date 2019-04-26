package com.example.jorgelapenaanton.chatapp.viewmodel.chat

import androidx.lifecycle.ViewModel
import com.example.domain.chat.usecase.*

class ChatViewModel(
    private val getUsers: GetUsersUseCase,
    private val setUsers: SetUsersUseCase,
    private val getConversations: GetConversationsUseCase,
    private val setConversation: SetConversationUseCase,
    private val getMessageUseCase: GetMessageUseCase,
    private val setMessageUseCase: SetMessageUseCase
    ) : ViewModel() {

        fun getUsers() = getUsers.execute()

        fun setUsers(users: String) = setUsers.execute(users)

        fun getConversations() = getConversations.execute()

        fun setConversation(identifier: String) = setConversation.execute(identifier)

        fun upadteMessages(identifier: String) = getMessageUseCase.execute(identifier)

        fun setMessage(identifier: String, user: String, message: String) = setMessageUseCase.execute(identifier, user, message)

        fun getStringConversationIdentifier(user: String, userCurrent: String, rotate: Boolean): String {
            return if (rotate){
                "$user-$userCurrent"
            } else {
                "$userCurrent-$user"
            }
        }
}