package com.test.swissborgcrypto.utils

fun pairSubscriptionText(pairName: String) =
    "{\n" +
        "   \"event\":\"subscribe\",\n" +
        "   \"channel\":\"ticker\",\n" +
        "   \"pair\":\"$pairName\"\n" +
        "}"

fun bookSubscriptionText(pairName: String) =
    "{\n" +
        "   \"event\":\"subscribe\",\n" +
        "   \"channel\":\"book\",\n" +
        "   \"pair\":\"$pairName\",\n" +
        "   \"prec\":\"P0\",\n" +
        "   \"freq\":\"F0\"\n" +
        "}"

const val cryptoDataFragment = "CryptoDataFragment"
const val maximumOrderCapacity = 14