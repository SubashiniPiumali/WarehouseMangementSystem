package com.example.warehouseapp.customer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auth0.android.jwt.JWT
import com.example.warehouseapp.R
import com.example.warehouseapp.adapter.CustomerOrderHistroryAdapter
import com.example.warehouseapp.api.ApiService
import com.example.warehouseapp.api.RetrofitClient
import com.example.warehouseapp.databinding.FragmentCustomerOrderHistroyBinding
import com.example.warehouseapp.model.ItemDetails
import com.example.warehouseapp.model.Order
import com.example.warehouseapp.model.OrderDetails
import com.example.warehouseapp.model.OrderItemRequest
import com.example.warehouseapp.model.OrderRequest
import com.example.warehouseapp.model.OrdersResponse
import com.example.warehouseapp.util.readBaseUrl
import okhttp3.internal.wait
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class CustomerOrderHistroyFragment : Fragment() {

    private lateinit var binding: FragmentCustomerOrderHistroyBinding
    private lateinit var apiService: ApiService
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var customerOrderHistroy: CustomerOrderHistroryAdapter
    private var customerId: String = ""
    private lateinit var orderTotal : TextView
    private lateinit var orderDate : TextView
    private lateinit var backImageView: ImageView
    private lateinit var carticon: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCustomerOrderHistroyBinding.inflate(inflater, container, false)
        val view = binding.root

        val baseUrl = readBaseUrl(requireContext())
        apiService = RetrofitClient.getRetrofitInstance(baseUrl).create(ApiService::class.java)

        val sharedPref2 = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = sharedPref2.getString("jwt_token", null)

        if (token != null) {
            val jwt = JWT(token)
            customerId = jwt.getClaim("_id").asString() ?: ""
        }
        // Retrieve the orderId from the arguments
        var orderId = arguments?.getString("orderId")
        var orderTotalX = arguments?.getString("orderTotal")
        var orderDateX = arguments?.getString("orderDate")
        if (orderId != null) {
            // Log or use the orderId
            println("Order ID: $orderId")
        }
        orderRecyclerView = binding.orderHistoryRecyclerView
        orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        customerOrderHistroy = CustomerOrderHistroryAdapter(emptyList(), apiService)
        orderRecyclerView.adapter = customerOrderHistroy
        orderTotal = binding.textViewOrderTotalValue
        orderDate = binding.orderDateValue
        backImageView = binding.backButton
        carticon = binding.cartIcon

        orderTotal.setText("$" + orderTotalX)
        orderDate.setText(orderDateX)
        if (orderId != null) {
            fetchOrderDetails(orderId)
        }

        backImageView.setOnClickListener {

            val customerHistoryFragment = CustomerHistoryFragment()

            // Navigate to the CustomerOrderHistoryFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_container, customerHistoryFragment) // Replace with your container ID
                .addToBackStack(null) // Add to back stack to allow navigation back
                .commit()
        }

        // On clicking the cart icon, create an Order and add OrderItems to it
        carticon.setOnClickListener {
            val intent = Intent(requireContext(), CustomerCartActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    private fun fetchOrderDetails(orderId: String) {
        apiService.getOrdersByOrderID(orderId).enqueue(object : Callback<OrderDetails> {
            override fun onResponse(call: Call<OrderDetails>, response: Response<OrderDetails>) {
                Log.d("fetchAllOrderHistory1", "Response Code: ${response.code()}")
                Log.d("fetchAllOrderHistory1", "Response Body: ${response.body()}")

                if (response.isSuccessful && response.body() != null) {
                    val orderDetails = response.body()
                    val itemDetailsList = orderDetails?.items // Use emptyList if items is null

                    // Check if fragment is still added before updating the adapter
                    if (isAdded) {
                        if (itemDetailsList != null) {
                        customerOrderHistroy.updateOrderList(itemDetailsList)
                        } // Pass the list of items to the adapter
                    }
                } else {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Failed to load orders", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<OrderDetails>, t: Throwable) {
                Log.e("CustomerHomeFragment", "Error fetching orders", t)
                // Use a safe context to avoid crashes
                if (isAdded) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

}