package com.adilstudio.project.onevault.presentation.password

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class PasswordTemplate(
    val useTemplate: Boolean = false,
    val rules: List<SerializablePasswordRule> = emptyList()
)

@Serializable
sealed class SerializablePasswordRule {
    abstract val id: String

    @Serializable
    data class FromServiceName(
        override val id: String,
        val length: Int,
        val casing: String // Store as string for serialization
    ) : SerializablePasswordRule()

    @Serializable
    data class FromUserName(
        override val id: String,
        val length: Int,
        val casing: String
    ) : SerializablePasswordRule()

    @Serializable
    data class FixedString(
        override val id: String,
        val value: String
    ) : SerializablePasswordRule()
}

object PasswordTemplateHelper {
    private val json = Json { ignoreUnknownKeys = true }

    fun serializeTemplate(useTemplate: Boolean, rules: List<PasswordRule>): String? {
        if (!useTemplate || rules.isEmpty()) return null

        val serializableRules = rules.map { rule ->
            when (rule) {
                is PasswordRule.FromServiceName -> SerializablePasswordRule.FromServiceName(
                    id = rule.id,
                    length = rule.length,
                    casing = rule.casing.name
                )
                is PasswordRule.FromUserName -> SerializablePasswordRule.FromUserName(
                    id = rule.id,
                    length = rule.length,
                    casing = rule.casing.name
                )
                is PasswordRule.FixedString -> SerializablePasswordRule.FixedString(
                    id = rule.id,
                    value = rule.value
                )
            }
        }

        val template = PasswordTemplate(useTemplate = true, rules = serializableRules)
        return json.encodeToString(template)
    }

    fun deserializeTemplate(templateJson: String?): Pair<Boolean, List<PasswordRule>> {
        if (templateJson.isNullOrBlank()) return false to emptyList()

        try {
            val template = json.decodeFromString<PasswordTemplate>(templateJson)
            val rules = template.rules.map { serializableRule ->
                when (serializableRule) {
                    is SerializablePasswordRule.FromServiceName -> PasswordRule.FromServiceName(
                        id = serializableRule.id,
                        length = serializableRule.length,
                        casing = Casing.valueOf(serializableRule.casing)
                    )
                    is SerializablePasswordRule.FromUserName -> PasswordRule.FromUserName(
                        id = serializableRule.id,
                        length = serializableRule.length,
                        casing = Casing.valueOf(serializableRule.casing)
                    )
                    is SerializablePasswordRule.FixedString -> PasswordRule.FixedString(
                        id = serializableRule.id,
                        value = serializableRule.value
                    )
                }
            }
            return template.useTemplate to rules
        } catch (e: Exception) {
            return false to emptyList()
        }
    }
}
