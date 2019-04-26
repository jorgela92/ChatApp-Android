package com.example.jorgelapenaanton.chatapp.viewmodel.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.domain.storage.usecase.GetImageUseCase
import com.example.domain.storage.usecase.SetImageUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("UNCHECKED_CAST")
@Singleton
class StorageViewModelFactory @Inject constructor(
    private val getImage: GetImageUseCase,
    private val setImage: SetImageUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StorageViewModel::class.java)) {
            return StorageViewModel(getImage, setImage) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}