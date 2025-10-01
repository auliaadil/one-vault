package com.adilstudio.project.onevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BillSplitResult(
    val participant: Participant,
    val assignedItems: List<BillItem>,
    val subtotal: Double,
    val taxAmount: Double,
    val serviceAmount: Double,
    val total: Double
)
