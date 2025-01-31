package com.example.warehouseapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.warehouseapp.R
import com.example.warehouseapp.customer.CustomerHistoryFragment
import com.example.warehouseapp.model.Order
import com.example.warehouseapp.model.OrderDetails
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class CustomerHistroyAdapter(

    private var orderList: List<OrderDetails>,
    private val listener: CustomerHistoryFragment
) : RecyclerView.Adapter<CustomerHistroyAdapter.CustomerHistroyViewHolder>(){

    fun updateOrderList(newOrder: List<OrderDetails>) {
        orderList = newOrder
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerHistroyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_customer_order_history, parent, false)
        return CustomerHistroyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerHistroyViewHolder, position: Int) {
        val item = orderList[position]
//
//        // Format the orderDate using SimpleDateFormat
//        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
//        val formattedDate = dateFormatter.format(item.orderDate)

        holder.orderDate.text = item.orderDate.toString()

        holder.orderStatus.text = item.status

        if (holder.orderStatus.text == "Pending") {
            holder.orderStatus.setTextColor(Color.parseColor("#006600")) // Green
        } else if  (   holder.orderStatus.text == "Shipped"){
            holder.orderStatus.setTextColor(Color.parseColor("#FFA500")) // Orange
        } else if (   holder.orderStatus.text == "Delivered") {
            holder.orderStatus.setTextColor(Color.parseColor("#8080ff")) // Blue
        }

        holder.noItems.text = "Number of Products: " + item.items.size.toString()
        holder.paymentAmount.text = "Total price : $" + item.orderTotal.toString()
        // Handle item click
        holder.itemView.setOnClickListener {
            listener.onViewOrderDetails(item) // Call the listener with the selected item
        }




    }
    override fun getItemCount() = orderList.size

    inner class CustomerHistroyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderDate: TextView = view.findViewById(R.id.orderDate)
        val orderStatus: TextView = view.findViewById(R.id.orderStatus)
        val noItems: TextView = view.findViewById(R.id.tvNoOfItems)
        val paymentAmount: TextView = view.findViewById(R.id.tvPaymentAmount)
    }
}