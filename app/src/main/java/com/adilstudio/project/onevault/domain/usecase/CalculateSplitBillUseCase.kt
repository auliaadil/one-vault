package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.*

class CalculateSplitBillUseCase {
    operator fun invoke(splitBill: SplitBill): List<BillSplitResult> {
        return when (splitBill.splitMethod) {
            SplitMethod.EQUAL -> calculateEqualSplit(splitBill)
            SplitMethod.BY_ITEM -> calculateBySplitItems(splitBill)
            SplitMethod.CUSTOM_RATIO -> calculateByCustomRatio(splitBill)
        }
    }

    private fun calculateEqualSplit(splitBill: SplitBill): List<BillSplitResult> {
        val participantCount = splitBill.participants.size
        if (participantCount == 0) return emptyList()

        val subtotalPerPerson = splitBill.subtotal / participantCount
        val taxPerPerson = calculateTaxPerParticipant(splitBill, subtotalPerPerson)
        val servicePerPerson = calculateServicePerParticipant(splitBill, subtotalPerPerson)

        return splitBill.participants.map { participant ->
            BillSplitResult(
                participant = participant,
                assignedItems = splitBill.items,
                subtotal = subtotalPerPerson,
                taxAmount = taxPerPerson,
                serviceAmount = servicePerPerson,
                total = subtotalPerPerson + taxPerPerson + servicePerPerson
            )
        }
    }

    private fun calculateBySplitItems(splitBill: SplitBill): List<BillSplitResult> {
        return splitBill.participants.map { participant ->
            val assignedItems = splitBill.items.filter { item ->
                item.assignedParticipantIds.contains(participant.id)
            }

            val subtotal = assignedItems.sumOf { item ->
                val shareCount = item.assignedParticipantIds.size
                if (shareCount > 0) item.subtotal / shareCount else 0.0
            }

            val taxAmount = calculateTaxPerParticipant(splitBill, subtotal)
            val serviceAmount = calculateServicePerParticipant(splitBill, subtotal)

            BillSplitResult(
                participant = participant,
                assignedItems = assignedItems,
                subtotal = subtotal,
                taxAmount = taxAmount,
                serviceAmount = serviceAmount,
                total = subtotal + taxAmount + serviceAmount
            )
        }
    }

    private fun calculateByCustomRatio(splitBill: SplitBill): List<BillSplitResult> {
        val totalRatio = splitBill.participants.sumOf { it.ratio.toDouble() }
        if (totalRatio == 0.0) return emptyList()

        return splitBill.participants.map { participant ->
            val ratioFraction = participant.ratio / totalRatio
            val subtotal = splitBill.subtotal * ratioFraction
            val taxAmount = calculateTaxPerParticipant(splitBill, subtotal)
            val serviceAmount = calculateServicePerParticipant(splitBill, subtotal)

            BillSplitResult(
                participant = participant,
                assignedItems = splitBill.items,
                subtotal = subtotal,
                taxAmount = taxAmount,
                serviceAmount = serviceAmount,
                total = subtotal + taxAmount + serviceAmount
            )
        }
    }

    private fun calculateTaxPerParticipant(splitBill: SplitBill, participantSubtotal: Double): Double {
        return when (splitBill.taxDistributionMethod) {
            TaxDistributionMethod.PROPORTIONAL -> {
                if (splitBill.subtotal > 0) {
                    splitBill.taxAmount * (participantSubtotal / splitBill.subtotal)
                } else 0.0
            }
            TaxDistributionMethod.EVEN_SPLIT -> {
                if (splitBill.participants.isNotEmpty()) {
                    splitBill.taxAmount / splitBill.participants.size
                } else 0.0
            }
        }
    }

    private fun calculateServicePerParticipant(splitBill: SplitBill, participantSubtotal: Double): Double {
        return when (splitBill.serviceDistributionMethod) {
            TaxDistributionMethod.PROPORTIONAL -> {
                if (splitBill.subtotal > 0) {
                    splitBill.serviceAmount * (participantSubtotal / splitBill.subtotal)
                } else 0.0
            }
            TaxDistributionMethod.EVEN_SPLIT -> {
                if (splitBill.participants.isNotEmpty()) {
                    splitBill.serviceAmount / splitBill.participants.size
                } else 0.0
            }
        }
    }
}
