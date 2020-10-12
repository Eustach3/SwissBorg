package com.test.swissborgcrypto.di.components

import com.test.swissborgcrypto.di.modules.DataFragmentModule
import com.test.swissborgcrypto.repositories.WebsocketRepository
import com.test.swissborgcrypto.ui.DataFragment
import com.test.swissborgcrypto.ui.MainActivity
import com.test.swissborgcrypto.viewmodels.DataFragmentViewModel
import com.test.swissborgcrypto.viewmodels.ViewModelFactory
import dagger.Component
import javax.inject.Singleton

@Component(
    modules =[DataFragmentModule::class]
)

@Singleton
interface CryptoDataComponent {
    fun inject(activity: MainActivity)
    fun inject(dataFragmentViewModel: DataFragmentViewModel)
    fun inject(websocketRepository: WebsocketRepository)
    fun inject(viewModelFactory: ViewModelFactory)
    fun inject(dataFragment: DataFragment)
}