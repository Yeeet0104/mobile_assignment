package com.example.mobile_assignment

data class WaterRecordData(
    val dayAdded: String,
    val timeAdded: String,
    val amountConsumed: String
){
    // Empty constructor
    constructor() : this("", "", "")
}

