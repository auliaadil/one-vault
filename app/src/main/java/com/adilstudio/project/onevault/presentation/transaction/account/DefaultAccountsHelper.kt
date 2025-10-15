package com.adilstudio.project.onevault.presentation.transaction.account

import com.adilstudio.project.onevault.domain.model.Account

fun createDefaultAccounts(): List<Account> {
    val currentTime = System.currentTimeMillis()

    return listOf(
        Account(
            id = 1L,
            name = "Cash",
            amount = 0.0,
            description = "Physical cash wallet",
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        Account(
            id = 2L,
            name = "BCA Savings",
            amount = 0.0,
            description = "BCA Bank savings account",
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        Account(
            id = 3L,
            name = "Mandiri Current",
            amount = 0.0,
            description = "Mandiri Bank current account",
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        Account(
            id = 4L,
            name = "BNI Savings",
            amount = 0.0,
            description = "BNI Bank savings account",
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        Account(
            id = 5L,
            name = "BRI Savings",
            amount = 0.0,
            description = "BRI Bank savings account",
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        Account(
            id = 6L,
            name = "Digital Wallet",
            amount = 0.0,
            description = "GoPay, OVO, DANA, ShopeePay, etc.",
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        Account(
            id = 7L,
            name = "Credit Card",
            amount = 0.0,
            description = "Credit card account (can have negative balance)",
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        )
    )
}
