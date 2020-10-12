package com.test.swissborgcrypto.bindingadapters

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.test.swissborgcrypto.adapters.OrderBookRecyclingAdapter
import com.test.swissborgcrypto.customclasses.BookOrderFixedSizeSet
import com.test.swissborgcrypto.ui.CryptoOrdersMeter

@BindingAdapter("orderBookList")
fun setOrderBookData(recyclerView: RecyclerView, ordersList: BookOrderFixedSizeSet?) {
    ordersList?.let {
        if (recyclerView.adapter == null) {
            recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
            val orderRecyclingAdapter = OrderBookRecyclingAdapter(ordersList.toMutableList())
            recyclerView.adapter = orderRecyclingAdapter
        }
        else {
            (recyclerView.adapter as OrderBookRecyclingAdapter).refresh(ordersList)
        }
    }
}

@BindingAdapter("orderMeterLevel")
fun setOrderMeterLevel(cryptoOrdersMeter: CryptoOrdersMeter, level: Int) {
    cryptoOrdersMeter.setBidLevel(level)
}