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
    OTHER
}

@Serializable
data class BillCategory(
    val id: String,
    val name: String,
    val icon: String,
    val color: String,
    val type: CategoryType,
    val parentCategoryId: String? = null,
    val isEditable: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)
