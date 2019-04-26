package com.example.domain.core

import io.reactivex.Completable

interface CompletableWithEmailCase<in String> {

    fun execute(t: String): Completable
}