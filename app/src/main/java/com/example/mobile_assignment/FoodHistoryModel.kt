package com.example.mobile_assignment

data class FoodHistoryModel(
    var dateHistory: String?,
    var dailyCalories: String?


)  : java.io.Serializable {
    // Empty constructor
    constructor() : this("", "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodHistoryModel

        if (dateHistory != other.dateHistory) return false
        if (dailyCalories != other.dailyCalories) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dateHistory?.hashCode() ?: 0
        result = 31 * result + (dailyCalories?.hashCode() ?: 0)
        return result
    }
}
