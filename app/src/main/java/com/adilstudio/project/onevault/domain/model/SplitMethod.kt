package com.adilstudio.project.onevault.domain.model

enum class SplitMethod {
    EQUAL,      // Divide total evenly among all participants
    BY_ITEM,    // Assign specific items to specific participants
    CUSTOM_RATIO // Use custom ratios/percentages per participant
}
