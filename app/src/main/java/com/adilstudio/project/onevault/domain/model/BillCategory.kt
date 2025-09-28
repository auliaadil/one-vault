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
data class BillCategory(
    val id: Long?,
    val name: String,
    val icon: String,
    val color: String,
    val type: CategoryType,
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
