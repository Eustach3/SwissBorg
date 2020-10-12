package com.test.swissborgcrypto.models


data class BookOrder(val name: String, var price : Double, var count: Int, var amount: Double) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BookOrder
        if (price != other.price) return false
        return true
    }

    override fun hashCode(): Int {
        return price.hashCode()
    }
}