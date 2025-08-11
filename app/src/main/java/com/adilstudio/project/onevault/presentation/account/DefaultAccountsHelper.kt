package com.adilstudio.project.onevault.presentation.account

import com.adilstudio.project.onevault.domain.model.Account
import java.util.UUID

fun createDefaultAccounts(): List<Account> {
    val currentTime = System.currentTimeMillis()

    return listOf(
        Account(
            id = UUID.randomUUID().toString(),
            name = "Cash",
            amount = 0.0,
            description = "Physical cash wallet",
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        Account(
            id = UUID.randomUUID().toString(),
            name = "BCA Savings",
            amount = 0.0,
            description = "BCA Bank savings account",
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        Account(
            id = UUID.randomUUID().toString(),
            name = "Mandiri Current",
            amount = 0.0,
            description = "Mandiri Bank current account",
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        Account(
            id = UUID.randomUUID().toString(),
            name = "BNI Savings",
            amount = 0.0,
            description = "BNI Bank savings account",
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        Account(
            id = UUID.randomUUID().toString(),
            name = "BRI Savings",
            amount = 0.0,
            description = "BRI Bank savings account",
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        Account(
            id = UUID.randomUUID().toString(),
            name = "Digital Wallet",
            amount = 0.0,
            description = "GoPay, OVO, DANA, ShopeePay, etc.",
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        Account(
            id = UUID.randomUUID().toString(),
            name = "Credit Card",
            amount = 0.0,
            description = "Credit card account (can have negative balance)",
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        )
    )
}
