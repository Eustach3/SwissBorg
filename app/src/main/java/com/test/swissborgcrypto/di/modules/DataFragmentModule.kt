package com.test.swissborgcrypto.di.modules

import com.test.swissborgcrypto.repositories.WebsocketRepository
import dagger.Module
import dagger.Provides
import okhttp3.Request
import okhttp3.Request.Builder
import javax.inject.Singleton

@Module
class DataFragmentModule {

    @Singleton
    @Provides
    fun providePairRepository() : WebsocketRepository = WebsocketRepository(provideBitfinexWebSocketBuilder())

    @Singleton
    @Provides
    fun provideBitfinexWebSocketBuilder() : Request =  Builder().url("ws://api.bitfinex.com/ws/1").build()

}