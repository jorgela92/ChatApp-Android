package com.example.domain.storage.usecase

import com.example.domain.core.SingleUploadImageWithParamsUseCase
import com.example.domain.storage.repository.StorageRepository

class SetImageUseCase(private val repository: StorageRepository) : SingleUploadImageWithParamsUseCase {

    override fun execute(conversation: String?, nameImage: String, imageByteArray: ByteArray) = repository.setImage(conversation, nameImage, imageByteArray)
}