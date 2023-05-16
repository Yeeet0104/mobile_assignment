package com.example.mobile_assignment

data class CaloriesDailyTarget(
    val caloriesDate: String,
    var caloriesTarget: Int
){
    // Empty constructor
    constructor() : this("", 1600)
}
