package com.example.domain.core

import io.reactivex.Completable

interface SingleUploadImageWithParamsUseCase {
    fun execute(conversation: String?, nameImage: String, imageByteArray: ByteArray): Completable
}