package com.adilstudio.project.onevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Credential(
    val id: Long,
    val serviceName: String,
    val username: String,
    val encryptedPassword: String,
    val category: String
)

