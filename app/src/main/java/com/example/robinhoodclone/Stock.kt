package com.example.robinhoodclone

data class Stock(
    val id: String, // unique ID — used as LazyColumn key
    val symbol: String, // e.g. "AAPL"
    val companyName: String, // e.g. "Apple Inc."
    val price: Double, // e.g. 182.50
    val changePercent: Double // e.g. +2.4 or -1.3
)
