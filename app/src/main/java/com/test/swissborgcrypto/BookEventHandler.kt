package com.test.swissborgcrypto

import com.test.swissborgcrypto.models.BookOrder

interface BookEventHandler : WebsocketEventHandler {
    fun onReceived(order: BookOrder)
    fun onError()
    fun onStart()
    fun onClose()
}