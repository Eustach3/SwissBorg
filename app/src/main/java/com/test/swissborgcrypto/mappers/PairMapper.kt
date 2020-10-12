package com.test.swissborgcrypto.mappers

import com.test.swissborgcrypto.models.CryptoPair
import com.test.swissborgcrypto.utils.round
import org.json.JSONArray

class PairMapper {

    fun jsonToCrypoPair(jsonArray: JSONArray, pairName: String): CryptoPair {
        return CryptoPair(
            pairName,
            jsonArray.getDouble(7).toFloat().round(2),
            jsonArray.getDouble(8).toFloat().round(2),
            jsonArray.getDouble(9).toFloat().round(2),
            jsonArray.getDouble(10).toFloat().round(2)
        )
    }
}