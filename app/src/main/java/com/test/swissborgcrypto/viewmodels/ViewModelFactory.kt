package com.test.swissborgcrypto.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.test.swissborgcrypto.repositories.WebsocketRepository
import javax.inject.Inject

class ViewModelFactory @Inject constructor(val repository: WebsocketRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return  modelClass.getConstructor(WebsocketRepository::class.java).newInstance(repository)
    }
}