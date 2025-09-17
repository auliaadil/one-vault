package com.adilstudio.project.onevault.presentation.password

/**
 * Enum representing different text casing options for password rules
 */
enum class Casing {
    LOWER,      // lowercase
    UPPER,      // UPPERCASE
    CAPITALIZE  // Capitalize first letter
}

/**
 * Sealed class representing different types of password generation rules
 */
sealed class PasswordRule {
    abstract val id: String

    /**
     * Rule that takes X first characters from service name with specified casing
     */
    data class FromServiceName(
        override val id: String = "service_${System.currentTimeMillis()}",
        val length: Int = 3,
        val casing: Casing = Casing.LOWER
    ) : PasswordRule()

    /**
     * Rule that takes X first characters from user account with specified casing
     */
    data class FromUserName(
        override val id: String = "user_${System.currentTimeMillis()}",
        val length: Int = 3,
        val casing: Casing = Casing.LOWER
    ) : PasswordRule()

    /**
     * Rule that adds a fixed string to the password
     */
    data class FixedString(
        override val id: String = "fixed_${System.currentTimeMillis()}",
        val value: String = ""
    ) : PasswordRule()
}

/**
 * Extension function to apply casing to a string
 */
fun String.applyCasing(casing: Casing): String {
    return when (casing) {
        Casing.LOWER -> this.lowercase()
        Casing.UPPER -> this.uppercase()
        Casing.CAPITALIZE -> this.lowercase().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }
}
