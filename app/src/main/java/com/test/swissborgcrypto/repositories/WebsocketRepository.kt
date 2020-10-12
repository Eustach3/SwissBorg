package com.test.swissborgcrypto.repositories

import com.test.swissborgcrypto.BookEventHandler
import com.test.swissborgcrypto.PairEventHandler
import com.test.swissborgcrypto.WebsocketEventHandler
import com.test.swissborgcrypto.di.components.DaggerCryptoDataComponent
import com.test.swissborgcrypto.mappers.OrderMapper
import com.test.swissborgcrypto.mappers.PairMapper
import com.test.swissborgcrypto.models.CryptoPair
import com.test.swissborgcrypto.models.BookOrder
import com.test.swissborgcrypto.repositories.WebsocketRepository.SocketChannel.BOOK
import com.test.swissborgcrypto.repositories.WebsocketRepository.SocketChannel.TICKER
import com.test.swissborgcrypto.utils.bookSubscriptionText
import com.test.swissborgcrypto.utils.pairSubscriptionText
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONArray
import org.json.JSONTokener
import javax.inject.Inject

class WebsocketRepository @Inject constructor(val bitfinexWebSocketBuilder: Request) {
    private val okHttpClient: OkHttpClient = OkHttpClient()
    private lateinit var pairEventHandler: PairEventHandler
    private lateinit var orderEventHandler: BookEventHandler
    private val pairObservable = PublishSubject.create<CryptoPair>()
    fun pairObservable(): Observable<CryptoPair> = pairObservable
    private val bookBidObservable = PublishSubject.create<List<BookOrder>>()
    fun bookBidObservable(): Observable<List<BookOrder>> = bookBidObservable
    private val bookAskObservable = PublishSubject.create<List<BookOrder>>()
    fun bookAskObservable(): Observable<List<BookOrder>> = bookAskObservable

    init {
        DaggerCryptoDataComponent.create().inject(this)
    }

    fun startPairDataUpdate(pairHandler: PairEventHandler, pairName: String) {
        pairEventHandler = pairHandler
        val listener = EchoWebSocketListener(pairName, TICKER, pairEventHandler, pairObservable, null, null)
        okHttpClient.newWebSocket(bitfinexWebSocketBuilder, listener)
    }

    fun startBookDataUpdate(orderHandler: BookEventHandler, pairName: String) {
        orderEventHandler = orderHandler
        val listener = EchoWebSocketListener(pairName, BOOK, orderEventHandler, null, bookBidObservable, bookAskObservable)
        okHttpClient.newWebSocket(bitfinexWebSocketBuilder, listener)
    }

    private class EchoWebSocketListener(
        val pairName: String,
        val socketChannel: SocketChannel,
        val dataEventHandler: WebsocketEventHandler,
        val pairObservable: PublishSubject<CryptoPair>?,
        val bookBidObservable: PublishSubject<List<BookOrder>>?,
        val bookAskObservable: PublishSubject<List<BookOrder>>?
    ) : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            when (socketChannel) {
                TICKER -> {
                    webSocket.send(pairSubscriptionText(pairName))
                    dataEventHandler as PairEventHandler
                    dataEventHandler.onStart()
                }
                BOOK -> {
                    webSocket.send(bookSubscriptionText(pairName))
                    dataEventHandler as BookEventHandler
                    dataEventHandler.onStart()
                }
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            val jsonData: Any = (JSONTokener(text).nextValue())
            if (jsonData is JSONArray) {
                pairObservable?.let {
                    val hasNecessaryData = jsonData.length() >= 10
                    if (hasNecessaryData) {
                        val cryptoPair = PairMapper().jsonToCrypoPair(jsonData, pairName)
                        it.onNext(cryptoPair)
                        (dataEventHandler as PairEventHandler).onPairReceived(cryptoPair)
                    }
                }
                if (bookBidObservable != null && bookAskObservable != null) {
                    val dataArray = jsonData[1]
                    when (dataArray) {
                        is JSONArray -> {
                            val orderList = OrderMapper().jsonToBookOrderList(dataArray, pairName)
                            val bidOrderList = orderList.filter { it.amount > 0}
                            val askOrderList = orderList.filter { it.amount < 0}
                            bookBidObservable.onNext(bidOrderList)
                            bookAskObservable.onNext(askOrderList)
                        }
                        is Int -> {
                            val order = OrderMapper().jsonOrderToBookOrder(jsonData, pairName)
                            val isBidOrder = order.amount > 0
                            if (isBidOrder) bookBidObservable.onNext(listOf(order)) else bookAskObservable.onNext(listOf(order))
                        }
                    }
                }
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null)
            when (dataEventHandler) {
                is PairEventHandler -> dataEventHandler.onClose()
                is BookEventHandler -> dataEventHandler.onClose()
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            when (dataEventHandler) {
                is PairEventHandler -> dataEventHandler.onError()
                is BookEventHandler -> dataEventHandler.onError()
            }
        }
        companion object {
            private const val NORMAL_CLOSURE_STATUS = 1000
        }
    }

    enum class SocketChannel {
        TICKER, BOOK
    }
}