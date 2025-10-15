package com.adilstudio.project.onevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class CategoryType {
    UTILITIES,
    FOOD_AND_DINING,
    SHOPPING,
    TRANSPORTATION,
    ENTERTAINMENT,
    HEALTHCARE,
    EDUCATION,
    OTHERS
}

@Serializable
data class TransactionCategory(
    val id: Long?,
    val name: String, // e.g., Food, Internet, Salary
    val icon: String,
    val color: String,
    val type: CategoryType, // Subcategory type (UTILITIES, FOOD_AND_DINING, etc.)
    val transactionType: TransactionType, // EXPENSE or INCOME
    val parentCategoryId: Long? = null,
    val isEditable: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun getIconAndName(): String {
        return if (icon.isEmpty()) {
            name
        } else {
            "$icon $name"
        }
    }
}
