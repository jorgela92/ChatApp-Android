package com.example.domain.auth.repository

import io.reactivex.Completable

interface AuthRepository {

    fun signup(username: String, password: String): Completable

    fun login(username: String, password: String): Completable

    fun logautUser()

    fun getUserAuth(): String?
}