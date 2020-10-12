package com.test.swissborgcrypto.customclasses

import com.test.swissborgcrypto.models.BookOrder
import com.test.swissborgcrypto.utils.maximumOrderCapacity
import java.util.*

class BookOrderFixedSizeSet(val maximumCapacity: Int, val orderType: OrderType) : TreeSet<BookOrder>(kotlin.Comparator { o1, o2 ->
    if (orderType ==OrderType.ASCENDING) o1.price.compareTo(o2.price) else o2.price.compareTo(o1.price)
}) {

    override fun add(
        element: BookOrder
    ): Boolean {
        if (contains(element)) remove(element)
        val added = super.add(element)
        while (added && size > maximumCapacity) {
            super.remove(elementAt(size - 1))
        }
        return added
    }

    override fun addAll(elements: Collection<BookOrder>): Boolean {
        return super.addAll(elements.take(maximumOrderCapacity))
    }

    enum class OrderType {
        ASCENDING, DESCENDING
    }
}