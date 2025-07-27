package com.adilstudio.project.onevault.domain.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Bill(
    val id: Long,
    val title: String,
    val category: String? = null, // Optional category
    val amount: Double,
    val vendor: String,
    val billDate: String, // Store as ISO date string (YYYY-MM-DD)
    val imagePath: String? = null // Optional image path
)
