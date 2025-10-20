package com.adilstudio.project.onevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SplitParticipant(
    val id: Long = 0L,
    val splitBillId: Long,
    val name: String,
    val shareAmount: Double = 0.0,
    val note: String? = null
)
