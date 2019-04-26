package com.example.domain.core

import io.reactivex.Observable

interface ObservableWithStorage<T> {

    fun execute(conversation: String?, nameImage: String): Observable<T>
}