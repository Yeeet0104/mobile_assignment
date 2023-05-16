package com.example.mobile_assignment

data class WaterDailyTarget(
    val date: String,
    var target: Int
){
    // Empty constructor
    constructor() : this("", 1600)
}
