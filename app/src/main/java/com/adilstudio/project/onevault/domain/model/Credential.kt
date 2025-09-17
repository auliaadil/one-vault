package com.adilstudio.project.onevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Credential(
    val id: Long = 0L,
    val serviceName: String,
    val username: String,
    val password: String,
    val passwordTemplate: String? = null, // JSON string storing password generation rules
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
