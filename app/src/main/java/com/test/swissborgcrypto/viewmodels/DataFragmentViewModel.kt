package com.test.swissborgcrypto.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.swissborgcrypto.BookEventHandler
import com.test.swissborgcrypto.PairEventHandler
import com.test.swissborgcrypto.customclasses.BookOrderFixedSizeSet
import com.test.swissborgcrypto.customclasses.BookOrderFixedSizeSet.OrderType.ASCENDING
import com.test.swissborgcrypto.customclasses.BookOrderFixedSizeSet.OrderType.DESCENDING
import com.test.swissborgcrypto.di.components.DaggerCryptoDataComponent
import com.test.swissborgcrypto.models.CryptoPair
import com.test.swissborgcrypto.models.BookOrder
import com.test.swissborgcrypto.repositories.WebsocketRepository
import com.test.swissborgcrypto.utils.maximumOrderCapacity
import com.test.swissborgcrypto.viewmodels.DataFragmentViewModel.WebsocketDataStatus.CLOSED
import com.test.swissborgcrypto.viewmodels.DataFragmentViewModel.WebsocketDataStatus.ERROR
import com.test.swissborgcrypto.viewmodels.DataFragmentViewModel.WebsocketDataStatus.STARTED
import com.test.swissborgcrypto.viewmodels.DataFragmentViewModel.WebsocketDataStatus.UPDATING
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.absoluteValue

class DataFragmentViewModel @Inject constructor(val repository: WebsocketRepository) : ViewModel() {

    private lateinit var pairEventHandler: PairEventHandler
    private lateinit var bookEventHandler: BookEventHandler
    var _btcPairMutableLiveData = MutableLiveData<CryptoPair>()
    val btcPairLiveData: LiveData<CryptoPair> = _btcPairMutableLiveData
    var _bidOrderBookMutableLiveData = MutableLiveData<BookOrderFixedSizeSet>()
    val bidOrderBookLiveData: LiveData<BookOrderFixedSizeSet> = _bidOrderBookMutableLiveData
    var _askOrderBookMutableLiveData = MutableLiveData<BookOrderFixedSizeSet>()
    val askOrderBookLiveData: LiveData<BookOrderFixedSizeSet> = _askOrderBookMutableLiveData
    var _pairDataStatus: MutableLiveData<WebsocketDataStatus> = MutableLiveData()
    val pairDataStatus: LiveData<WebsocketDataStatus> = _pairDataStatus
    var _orderBookDataStatus: MutableLiveData<WebsocketDataStatus> = MutableLiveData()
    val orderDataStatus: LiveData<WebsocketDataStatus> = _orderBookDataStatus
    var _bookOrderBidPercentage: MutableLiveData<Int> = MutableLiveData()
    val bookOrderBidPercentage: LiveData<Int> = _bookOrderBidPercentage
    val cd = CompositeDisposable()

    init {
        DaggerCryptoDataComponent.create().inject(this)
        initHandlers()
        initDataStatus()
        startObservingBtc()
    }

    private fun initDataStatus() {
        _pairDataStatus.value = CLOSED
        _orderBookDataStatus.value = CLOSED
    }

    private fun startObservingBtc() {
        repository.startPairDataUpdate(pairEventHandler, "BTCUSD")
        cd.add(repository.pairObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                updatePairData(it)
                startBookOrderUpdate()
            },
                {
                    Timber.e(it)
                    _pairDataStatus.value = ERROR
                }
            )
        )
    }

    private fun updatePairData(it: CryptoPair?) {
        _btcPairMutableLiveData.postValue(it)
        _pairDataStatus.value = STARTED
    }

    private fun startBookOrderUpdate() {
        if (orderDataStatus.value == CLOSED) {
            _orderBookDataStatus.value = STARTED
            repository.startBookDataUpdate(bookEventHandler, "BTCUSD")
            startObservingBookOrders()
        }
    }

    fun startObservingBookOrders() {
        cd.add(repository.bookBidObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ bidOrders ->
                if (_bidOrderBookMutableLiveData.value == null || _bidOrderBookMutableLiveData.value.isNullOrEmpty()) {
                    updateBidsWithReceivedList(bidOrders)
                } else {
                    _btcPairMutableLiveData.value?.let { btcPair ->
                        updateBidsWithReceivedOrder(bidOrders, btcPair)
                    }
                }
            }, {
                Timber.e(it)
                _orderBookDataStatus.value = ERROR
            }
            )
        )
        cd.add(repository.bookAskObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ askOrders ->
                if (_askOrderBookMutableLiveData.value == null || _askOrderBookMutableLiveData.value.isNullOrEmpty()) {
                    updateAsksWithReceivedList(askOrders)
                } else {
                    _btcPairMutableLiveData.value?.let { btcPair ->
                        updateAsksWithReceivedOrder(askOrders, btcPair)
                    }
                }
            }, {
                Timber.e(it)
                _orderBookDataStatus.value = ERROR
            }
            )
        )
    }

    private fun updateAsksWithReceivedOrder(
        askOrders: List<BookOrder>,
        btcPair: CryptoPair
    ) {
        val currentList = _askOrderBookMutableLiveData.value
        val receivedOrder = askOrders?.firstOrNull()
        if (receivedOrder != null && receivedOrder.price > btcPair.lastPrice) {
            currentList?.add(receivedOrder)
            _askOrderBookMutableLiveData.postValue(currentList)
            updateBookOrderPercentage()
        }
    }

    private fun updateAsksWithReceivedList(askOrders: List<BookOrder>) {
        val newList = BookOrderFixedSizeSet(maximumOrderCapacity, ASCENDING)
        _btcPairMutableLiveData.value?.let { btcPair ->
            val relevantAskList = askOrders.filter { it.price > btcPair.lastPrice }
            newList.addAll(relevantAskList)
            _askOrderBookMutableLiveData.postValue(newList)
            updateBookOrderPercentage()
        }
    }

    private fun updateBidsWithReceivedOrder(
        bidOrders: List<BookOrder>,
        btcPair: CryptoPair
    ) {
        val currentList = _bidOrderBookMutableLiveData.value
        val receivedOrder = bidOrders.firstOrNull()
        if (receivedOrder != null && receivedOrder.price < btcPair.lastPrice) {
            currentList?.add(receivedOrder)
            _bidOrderBookMutableLiveData.postValue(currentList)
            updateBookOrderPercentage()
        }
    }

    private fun updateBidsWithReceivedList(bidOrders: List<BookOrder>) {
        val newList = BookOrderFixedSizeSet(maximumOrderCapacity, DESCENDING)
        _btcPairMutableLiveData.value?.let { btcPair ->
            val relevantBidList = bidOrders.filter { it.price < btcPair.lastPrice }
            newList.addAll(relevantBidList)
            _bidOrderBookMutableLiveData.value = newList
            updateBookOrderPercentage()
        }
    }

    fun updateBookOrderPercentage() {
        var totalBidsAmount = 0
        var totalAsksAmount = 0
        bidOrderBookLiveData.value?.forEach { totalBidsAmount += it.amount.toInt() }
        askOrderBookLiveData.value?.forEach { totalAsksAmount += it.amount.toInt() }
        if (totalAsksAmount != 0 && totalBidsAmount != 0) {
            val bidPercentage = totalBidsAmount * 100 / (totalAsksAmount.absoluteValue + totalBidsAmount)
            _bookOrderBidPercentage.postValue(bidPercentage)
        }
    }

    private fun initHandlers() {
        bookEventHandler = object : BookEventHandler {
            override fun onError() {
                _orderBookDataStatus.postValue(ERROR)
            }

            override fun onStart() {
                _orderBookDataStatus.postValue(STARTED)
            }

            override fun onClose() {
                _orderBookDataStatus.postValue(CLOSED)
            }

            override fun onReceived(order: BookOrder) {
                _orderBookDataStatus.postValue(UPDATING)
            }
        }
        pairEventHandler = object : PairEventHandler {

            override fun onPairReceived(pair: CryptoPair) {
                _pairDataStatus.postValue(STARTED)
            }

            override fun onError() {
                _pairDataStatus.postValue(ERROR)
            }

            override fun onStart() {
                _pairDataStatus.postValue(STARTED)
            }

            override fun onClose() {
                _pairDataStatus.postValue(CLOSED)
            }
        }
    }

    override fun onCleared() {
        cd.dispose()
        super.onCleared()
    }

    enum class WebsocketDataStatus {
        STARTED, UPDATING, CLOSED, ERROR
    }
}