package com.example.mobile_assignment

data class FoodModel(
    var foodId: String? = null,
    var foodName: String? = null,
    var foodCalories: String? = null
) : java.io.Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodModel

        if (foodId != other.foodId) return false
        if (foodName != other.foodName) return false
        if (foodCalories != other.foodCalories) return false

        return true
    }

    override fun hashCode(): Int {
        var result = foodId?.hashCode() ?: 0
        result = 31 * result + (foodName?.hashCode() ?: 0)
        result = 31 * result + (foodCalories?.hashCode() ?: 0)
        return result
    }
}
