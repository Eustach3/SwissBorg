package com.test.swissborgcrypto.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.test.swissborgcrypto.R
import com.test.swissborgcrypto.adapters.OrderBookRecyclingAdapter.OrderViewHolder
import com.test.swissborgcrypto.customclasses.BookOrderFixedSizeSet
import com.test.swissborgcrypto.models.BookOrder
import kotlinx.android.synthetic.main.order_book_item.view.*

class OrderBookRecyclingAdapter(val orderBookList: MutableList<BookOrder>) : RecyclerView.Adapter<OrderViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(
            R.layout.order_book_item,
            parent,
            false
        )
        return OrderViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bindOrderToView(orderBookList[position])
        if (position == 0) holder.showHeadings() else holder.hideHeadings()
    }

    override fun getItemCount(): Int {
        return orderBookList.size
    }

    fun refresh(orderBookList: BookOrderFixedSizeSet) {
        this.orderBookList.clear()
        this.orderBookList.addAll(orderBookList)
        notifyDataSetChanged()
    }

    class OrderViewHolder(val view: View) : ViewHolder(view) {
        lateinit var orderBook : BookOrder

        fun showHeadings() {
            val bidColor = view.context.resources.getColor(R.color.colorGreen, null)
            val askColor = view.context.resources.getColor(R.color.colorRed, null)
            val backgroundColor = if (orderBook.amount > 0) bidColor else askColor
            view.order_headings.background = ColorDrawable(backgroundColor)
            view.order_headings.visibility = View.VISIBLE
        }

        fun hideHeadings() {
            view.order_headings.visibility = View.GONE
        }

        fun bindOrderToView(bookOrder: BookOrder) {
            orderBook = bookOrder
            val formattedAmount = "%.2f".format(bookOrder.amount)
            view.order_price.text = bookOrder.price.toInt().toString()
            view.order_count.text = bookOrder.count.toString()
            view.order_amount.text = formattedAmount
        }
    }
}