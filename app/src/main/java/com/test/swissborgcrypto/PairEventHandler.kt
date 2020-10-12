package com.test.swissborgcrypto

import com.test.swissborgcrypto.models.CryptoPair

interface PairEventHandler : WebsocketEventHandler {
    fun onPairReceived(pair : CryptoPair)
    fun onError()
    fun onStart()
    fun onClose()
}