package com.adilstudio.project.onevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Participant(
    val id: Long = 0,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val ratio: Float = 1f // For custom ratio split
)
