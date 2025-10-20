package com.adilstudio.project.onevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LineItem(
    val description: String,
    val price: Double,
    val isExtraCharge: Boolean = false // true for tax, service fee, etc.
)

@Serializable
data class OcrResult(
    val merchant: String? = null,
    val title: String? = null,
    val lineItems: List<LineItem> = emptyList(),
    val tax: Double = 0.0,
    val serviceFee: Double = 0.0,
    val totalAmount: Double = 0.0,
    val rawText: String = ""
)
