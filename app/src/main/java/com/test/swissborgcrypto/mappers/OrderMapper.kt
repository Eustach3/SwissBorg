package com.test.swissborgcrypto.mappers

import com.test.swissborgcrypto.models.CryptoPair
import com.test.swissborgcrypto.models.BookOrder
import org.json.JSONArray

class OrderMapper {

    fun jsonToBookOrderList(jsonArray: JSONArray, pairName: String): List<BookOrder> {
        val bookOrderList = mutableListOf<BookOrder>()
        for (index in 0 until jsonArray.length()) {
            val bookOrder =  jsonItemListToBookOrder(jsonArray.getJSONArray(index), pairName)
            bookOrderList.add(bookOrder)
        }
        return bookOrderList
    }

    fun jsonItemListToBookOrder(jsonArray: JSONArray, pairName: String) :BookOrder{
        return BookOrder(
            pairName,
            jsonArray.getDouble(0),
            jsonArray.getDouble(1).toInt(),
            jsonArray.getDouble(2)
        )
    }
    fun jsonOrderToBookOrder(jsonArray: JSONArray, pairName: String) :BookOrder{
        return BookOrder(
            pairName,
            jsonArray.getDouble(1),
            jsonArray.getDouble(2).toInt(),
            jsonArray.getDouble(3)
        )
    }
}