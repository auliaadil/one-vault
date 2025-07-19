package com.adilstudio.project.onevault.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class VaultFile(
    val id: Long,
    val name: String,
    val category: String,
    val filePath: String
)

