package com.test.swissborgcrypto.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.test.swissborgcrypto.RxImmediateSchedulerRule
import com.test.swissborgcrypto.models.BookOrder
import com.test.swissborgcrypto.models.CryptoPair
import com.test.swissborgcrypto.repositories.WebsocketRepository
import com.test.swissborgcrypto.viewmodels.DataFragmentViewModel.WebsocketDataStatus
import com.test.swissborgcrypto.viewmodels.DataFragmentViewModel.WebsocketDataStatus.*
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.ClassRule
import org.junit.Rule
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.*
import kotlin.random.Random

class DataFragmentViewModelTest {

    lateinit var dataViewModel: DataFragmentViewModel
    @Mock
    private lateinit var dataObserver: Observer<WebsocketDataStatus>
    @Mock
    private lateinit var repository: WebsocketRepository
    @Mock
    private lateinit var pairObservable: Observable<CryptoPair>
    @Mock
    private lateinit var cryptoPair: CryptoPair

    @get:Rule
    var taskRule = InstantTaskExecutorRule()

    companion object {
        @ClassRule
        @JvmField
        val schedulers = RxImmediateSchedulerRule()
    }

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        Mockito.`when`(repository.pairObservable()).thenReturn(Observable.just(cryptoPair))
        Mockito.`when`(repository.bookAskObservable()).thenReturn(Observable.just(getRandomOrderList()))
        Mockito.`when`(repository.bookBidObservable()).thenReturn(Observable.just(getRandomOrderList()))
        dataViewModel = DataFragmentViewModel(repository)
        dataViewModel._pairDataStatus.observeForever(dataObserver)
        dataViewModel._orderBookDataStatus.observeForever(dataObserver)
    }

    @Test
    fun dataStatusStartedOnInit() {
        assertNotNull(dataViewModel._orderBookDataStatus.value)
        assertNotNull(dataViewModel._pairDataStatus.value)
        assert(dataViewModel._pairDataStatus.hasObservers())
        assert(dataViewModel._orderBookDataStatus.hasObservers())
        assert(dataViewModel._pairDataStatus.value == STARTED)
        assert(dataViewModel._orderBookDataStatus.value == STARTED)
    }

    @Test
    fun testPairDataUpdatedOnStart() {
        assertNotNull(repository.pairObservable().blockingFirst())
        assertNotNull(dataViewModel._btcPairMutableLiveData.value)
    }

    @Test
    fun testBookOrderDataUpdatedOnStart() {
        assertNotNull(repository.bookAskObservable().blockingFirst())
        assertNotNull(repository.bookBidObservable().blockingFirst())
        assertNotNull(dataViewModel._askOrderBookMutableLiveData.value)
        assertNotNull(dataViewModel._bidOrderBookMutableLiveData.value)
    }

    private fun getRandomOrderList() :List<BookOrder> {
        return listOf(
            getRndBookOrder(),
            getRndBookOrder()
        )
    }
    private fun getRndBookOrder() = BookOrder(getRndString(), getRndDouble(), getRndInt(), getRndDouble())
    private fun getRndString() = UUID.randomUUID().toString()
    private fun getRndInt() = Random.nextInt()
    private fun getRndDouble() = Random.nextDouble()
}