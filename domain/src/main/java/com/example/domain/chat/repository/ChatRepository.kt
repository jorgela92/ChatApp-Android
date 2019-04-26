package com.example.domain.chat.repository

import com.example.domain.chat.model.Message
import com.example.domain.chat.model.Users
import io.reactivex.Completable
import io.reactivex.Observable

interface ChatRepository {

    fun setUsers(users: String): Completable

    fun updatetUsers(): Observable<Users>

    fun getConversations(): Observable<Array<String>>

    fun setConversations(identifier: String): Completable

    fun updateMessage(identifier: String): Observable<Array<Message>>

    fun setMessage(identifier: String, user: String, message: String): Completable
}