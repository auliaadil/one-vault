package com.adilstudio.project.onevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SplitBill(
    val id: Long = 0L,
    val title: String,
    val merchant: String? = null,
    val date: String,
    val tax: Double = 0.0,
    val serviceFee: Double = 0.0,
    val totalAmount: Double,
    val imagePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val items: List<SplitItem> = emptyList(),
    val participants: List<SplitParticipant> = emptyList()
)
