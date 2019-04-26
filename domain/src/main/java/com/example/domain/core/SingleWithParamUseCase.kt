package com.example.domain.core

import io.reactivex.Completable

interface SingleWithParamUseCase {
    fun execute(username: String, password: String): Completable
}