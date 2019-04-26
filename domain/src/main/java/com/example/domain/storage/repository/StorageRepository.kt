package com.example.domain.storage.repository

import io.reactivex.Completable
import io.reactivex.Observable

interface StorageRepository {

    fun setImage(conversation: String?, nameImage: String, imageByteArray: ByteArray): Completable

    fun getImage(conversation: String?, nameImage: String): Observable<ByteArray>
}