package com.adilstudio.project.onevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BillItem(
    val id: Long = 0,
    val name: String,
    val price: Double,
    val quantity: Int = 1,
    val assignedParticipantIds: List<Long> = emptyList()
) {
    val subtotal: Double
        get() = price * quantity
}
