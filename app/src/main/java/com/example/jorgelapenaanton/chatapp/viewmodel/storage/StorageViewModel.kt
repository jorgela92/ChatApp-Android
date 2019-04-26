package com.example.jorgelapenaanton.chatapp.viewmodel.storage

import androidx.lifecycle.ViewModel
import com.example.domain.storage.usecase.GetImageUseCase
import com.example.domain.storage.usecase.SetImageUseCase

class StorageViewModel(private val getImage: GetImageUseCase, private val setImage: SetImageUseCase) : ViewModel() {

    fun getImage(conversation: String?, nameImage: String) =
        getImage.execute(conversation, nameImage)

    fun setImage(conversation: String?, nameImage: String, imageByteArray: ByteArray) =
        setImage.execute(conversation, nameImage, imageByteArray)
}