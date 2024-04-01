package com.example.android_hit.data

data class ScanResponse(
    val items : Items
)
data class Items(
    val items : List<Item>
)
data class Item(
    val name : String,
    val qty : Int,
    val price : Double
)