package com.adilstudio.project.onevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SplitItem(
    val id: Long = 0L,
    val splitBillId: Long,
    val description: String,
    val price: Double, // Price per individual item
    val assignedQuantities: Map<String, Int> = emptyMap() // Participant name to quantity mapping
) {
    // Helper to get total quantity across all participants
    val totalQuantity: Int get() = assignedQuantities.values.sum()

    // Helper to get total value (price * total quantity)
    val totalValue: Double get() = price * totalQuantity

    // Helper to get assigned participants (for backward compatibility)
    val assignedTo: List<String> get() = assignedQuantities.keys.toList()
}
