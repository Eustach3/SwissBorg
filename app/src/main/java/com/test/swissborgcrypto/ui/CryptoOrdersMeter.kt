package com.test.swissborgcrypto.ui

import android.content.Context
import android.graphics.drawable.ClipDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.test.swissborgcrypto.R
import com.test.swissborgcrypto.databinding.OrderMeterLayoutBinding
import kotlinx.android.synthetic.main.order_meter_layout.view.*

class CryptoOrdersMeter @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(
    context,
    attrs,
    defStyleAttr
) {
    private val meterBinding : OrderMeterLayoutBinding

    init {
        meterBinding  = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.order_meter_layout,
            this,
            true
        )
    }

    fun setBidLevel(bidPercentage : Int) {
        val mImageDrawable = meterBinding.orderMeterGreen.drawable as ClipDrawable
        mImageDrawable.level = bidPercentage * 100
        if (bidPercentage > 0 && bidPercentage < 100) {
            updateTextPercentage(bidPercentage)
            meterBinding.root.visibility = VISIBLE
        }

    }

    private fun updateTextPercentage(bidPercentage: Int) {
        val askPercentage = 100-bidPercentage
        bid_text_percentage.setText(resources.getString(R.string.crypto_meter_bid_percent, bidPercentage.toString()))
        ask_text_percentage.setText(resources.getString(R.string.crypto_meter_ask_percent, askPercentage.toString()))
    }
}
