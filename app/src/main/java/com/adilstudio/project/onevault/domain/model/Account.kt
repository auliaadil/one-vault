package com.adilstudio.project.onevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val id: String,
    val name: String,
    val amount: Double = 0.0,
    val description: String? = null,
    val isEditable: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
