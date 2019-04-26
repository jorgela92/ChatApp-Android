package com.example.domain.storage.usecase

import com.example.domain.core.ObservableWithStorage
import com.example.domain.storage.repository.StorageRepository
import io.reactivex.Observable

class GetImageUseCase(private val repository: StorageRepository) : ObservableWithStorage<ByteArray> {
    override fun execute(conversation: String?, nameImage: String): Observable<ByteArray> = repository.getImage(conversation, nameImage)
}