package com.example.domain.core

import io.reactivex.Completable

interface CompletableWithMessageCase {

    fun execute(identifier: String, user: String, t: String): Completable
}