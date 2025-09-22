package com.adilstudio.project.onevault.domain.model

data class AppLockTimeoutOption(
    val labelResId: Int,
    val valueMs: Long
) {
    companion object {
        const val FIVE_SECONDS = 5_000L
        const val THIRTY_SECONDS = 30_000L
        const val ONE_MINUTE = 60_000L
        const val FIVE_MINUTES = 300_000L
    }
}
