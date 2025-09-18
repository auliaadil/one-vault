package com.adilstudio.project.onevault.presentation.credential.credentialform

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.adilstudio.project.onevault.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRuleSection(
    onAddRule: (CredentialRule) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.password_rules),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

            Text(
                text = stringResource(R.string.add_rules_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))

            OutlinedButton(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium))
                )
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
                Text(stringResource(R.string.add_rule))
            }
        }
    }

    if (showAddDialog) {
        AddRuleDialog(
            onDismiss = { showAddDialog = false },
            onAddRule = { rule ->
                onAddRule(rule)
                showAddDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRuleDialog(
    onDismiss: () -> Unit,
    onAddRule: (CredentialRule) -> Unit
) {
    var selectedRuleType by remember { mutableStateOf(RuleType.SERVICE_NAME) }
    var length by remember { mutableStateOf("3") }
    var selectedCasing by remember { mutableStateOf(Casing.LOWER) }
    var fixedString by remember { mutableStateOf("") }
    var showRuleTypeDropdown by remember { mutableStateOf(false) }
    var showCasingDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_password_rule)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_large))
            ) {
                // Rule type selection
                ExposedDropdownMenuBox(
                    expanded = showRuleTypeDropdown,
                    onExpandedChange = { showRuleTypeDropdown = !showRuleTypeDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedRuleType.displayName,
                        onValueChange = {},
                        label = { Text(stringResource(R.string.rule_type)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRuleTypeDropdown)
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = showRuleTypeDropdown,
                        onDismissRequest = { showRuleTypeDropdown = false }
                    ) {
                        RuleType.entries.forEach { ruleType ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(ruleType.displayName)
                                        Text(
                                            text = ruleType.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    selectedRuleType = ruleType
                                    showRuleTypeDropdown = false
                                }
                            )
                        }
                    }
                }

                // Configuration based on rule type
                when (selectedRuleType) {
                    RuleType.SERVICE_NAME, RuleType.USER_ACCOUNT -> {
                        OutlinedTextField(
                            value = length,
                            onValueChange = { newLength ->
                                if (newLength.all { it.isDigit() } && newLength.length <= 2) {
                                    length = newLength
                                }
                            },
                            label = { Text(stringResource(R.string.number_of_characters)) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("3") }
                        )

                        ExposedDropdownMenuBox(
                            expanded = showCasingDropdown,
                            onExpandedChange = { showCasingDropdown = !showCasingDropdown }
                        ) {
                            OutlinedTextField(
                                value = selectedCasing.name.lowercase().replaceFirstChar { it.uppercase() },
                                onValueChange = {},
                                label = { Text(stringResource(R.string.text_casing)) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCasingDropdown)
                                }
                            )

                            ExposedDropdownMenu(
                                expanded = showCasingDropdown,
                                onDismissRequest = { showCasingDropdown = false }
                            ) {
                                Casing.entries.forEach { casing ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(casing.name.lowercase().replaceFirstChar { it.uppercase() })
                                                Text(
                                                    text = when (casing) {
                                                        Casing.LOWER -> stringResource(R.string.lowercase_text)
                                                        Casing.UPPER -> stringResource(R.string.uppercase_text)
                                                        Casing.CAPITALIZE -> stringResource(R.string.capitalize_first_letter)
                                                    },
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedCasing = casing
                                            showCasingDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    RuleType.FIXED_STRING -> {
                        OutlinedTextField(
                            value = fixedString,
                            onValueChange = { fixedString = it },
                            label = { Text(stringResource(R.string.fixed_string)) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(stringResource(R.string.fixed_string_placeholder)) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val rule = when (selectedRuleType) {
                        RuleType.SERVICE_NAME -> CredentialRule.FromServiceName(
                            length = length.toIntOrNull() ?: 3,
                            casing = selectedCasing
                        )
                        RuleType.USER_ACCOUNT -> CredentialRule.FromUserName(
                            length = length.toIntOrNull() ?: 3,
                            casing = selectedCasing
                        )
                        RuleType.FIXED_STRING -> CredentialRule.FixedString(
                            value = fixedString
                        )
                    }
                    onAddRule(rule)
                },
                enabled = when (selectedRuleType) {
                    RuleType.SERVICE_NAME, RuleType.USER_ACCOUNT -> length.isNotBlank() && length.toIntOrNull() != null && length.toInt() > 0
                    RuleType.FIXED_STRING -> fixedString.isNotBlank()
                }
            ) {
                Text(stringResource(R.string.add_rule))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

enum class RuleType(val displayNameRes: Int, val descriptionRes: Int) {
    SERVICE_NAME(R.string.from_service_name, R.string.from_service_name_description),
    USER_ACCOUNT(R.string.from_user_account, R.string.from_user_account_description),
    FIXED_STRING(R.string.fixed_string_rule, R.string.fixed_string_rule_description);

    val displayName: String
        @Composable
        get() = stringResource(displayNameRes)

    val description: String
        @Composable
        get() = stringResource(descriptionRes)
}
