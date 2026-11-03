package com.sb.woohoo.ui.main.fragments.fnb.order.pojo

import com.google.gson.annotations.SerializedName
import com.sb.woohoo.ui.main.fragments.fnb.products.pojo.Product

data class PlaceOrderResponse (val status: Int, val message: String,  @SerializedName("data")val orderData: OrderData?, val error: String)

data class OrderData(val currentStatus: String, val id: Int, val amount: String, val currency: String, val items: List<Product>)