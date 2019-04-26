package com.example.domain.core

import io.reactivex.Observable

interface ObservableMessageUseCase<T> {

    fun execute(identifier: String): Observable<T>
}