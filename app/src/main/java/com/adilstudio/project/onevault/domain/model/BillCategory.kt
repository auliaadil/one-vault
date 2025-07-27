package com.adilstudio.project.onevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class CategoryType {
    ESSENTIAL,
    LIFESTYLE,
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
