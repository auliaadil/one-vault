package com.adilstudio.project.onevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SplitBill(
    val id: Long = 0,
    val title: String,
    val vendor: String = "",
    val billDate: String,
    val imagePath: String? = null,
    val items: List<BillItem>,
    val participants: List<Participant>,
    val splitMethod: SplitMethod,
    val taxPercentage: Double = 0.0,
    val servicePercentage: Double = 0.0,
    val taxDistributionMethod: TaxDistributionMethod = TaxDistributionMethod.PROPORTIONAL,
    val serviceDistributionMethod: TaxDistributionMethod = TaxDistributionMethod.PROPORTIONAL,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val subtotal: Double
        get() = items.sumOf { it.subtotal }

    val taxAmount: Double
        get() = subtotal * (taxPercentage / 100.0)

    val serviceAmount: Double
        get() = subtotal * (servicePercentage / 100.0)

    val total: Double
        get() = subtotal + taxAmount + serviceAmount
}

enum class TaxDistributionMethod {
    PROPORTIONAL, // Distribute based on subtotal proportion
    EVEN_SPLIT    // Split evenly among all participants
}
