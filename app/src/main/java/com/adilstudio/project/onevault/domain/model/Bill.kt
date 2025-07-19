package com.adilstudio.project.onevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Bill(
    val id: Long,
    val title: String,
    val category: String,
    val amount: Double,
    val vendor: String,
    val billDate: Long,
    val imagePath: String
)

