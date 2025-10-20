package com.adilstudio.project.onevault.domain.manager

import com.adilstudio.project.onevault.domain.model.SplitItem
import com.adilstudio.project.onevault.domain.model.SplitParticipant
import kotlin.math.round

class SplitCalculator {

    /**
     * Calculates the share amount for each participant based on assigned items with quantities, tax, and service fee
     */
    fun calculateShares(
        items: List<SplitItem>,
        participants: List<SplitParticipant>,
        tax: Double = 0.0,
        serviceFee: Double = 0.0
    ): List<SplitParticipant> {
        if (participants.isEmpty()) return emptyList()

        // Calculate base amount per participant from assigned items with quantities
        val participantBaseAmounts = mutableMapOf<String, Double>()

        // Initialize all participants with 0.0
        participants.forEach { participant ->
            participantBaseAmounts[participant.name] = 0.0
        }

        // Calculate each participant's share based on quantity they're assigned
        items.forEach { item ->
            item.assignedQuantities.forEach { (participantName, quantity) ->
                val participantItemTotal = item.price * quantity
                participantBaseAmounts[participantName] =
                    (participantBaseAmounts[participantName] ?: 0.0) + participantItemTotal
            }
        }

        // Calculate total base amount (excluding tax and service fee)
        val totalBaseAmount = participantBaseAmounts.values.sum()

        if (totalBaseAmount == 0.0) {
            // If no items assigned, split everything equally
            val totalToSplit = items.sumOf { it.totalValue }
            val taxAmount = totalToSplit * (tax / 100.0)
            val serviceFeeAmount = totalToSplit * (serviceFee / 100.0)
            val sharePerPerson = (totalToSplit + taxAmount + serviceFeeAmount) / participants.size
            return participants.map { participant ->
                participant.copy(shareAmount = roundToTwoDecimals(sharePerPerson))
            }
        }

        // Apply tax and service fee as percentages of each participant's base amount
        return participants.map { participant ->
            val baseAmount = participantBaseAmounts[participant.name] ?: 0.0
            val participantTax = baseAmount * (tax / 100.0)
            val participantServiceFee = baseAmount * (serviceFee / 100.0)
            val totalShare = baseAmount + participantTax + participantServiceFee
            participant.copy(shareAmount = roundToTwoDecimals(totalShare))
        }
    }

    /**
     * Validates that all items have been assigned to participants
     */
    fun validateAssignments(items: List<SplitItem>): List<String> {
        val errors = mutableListOf<String>()

        items.forEach { item ->
            if (item.assignedQuantities.isEmpty()) {
                errors.add("${item.description} has not been assigned to any participants")
            }
        }

        return errors
    }

    /**
     * Calculates the total amount of the bill including all items, tax, and service fee
     */
    fun calculateTotalAmount(items: List<SplitItem>, tax: Double, serviceFee: Double): Double {
        val itemsTotal = items.sumOf { it.totalValue }
        return roundToTwoDecimals(itemsTotal + tax + serviceFee)
    }

    private fun roundToTwoDecimals(value: Double): Double {
        return round(value * 100) / 100
    }
}
