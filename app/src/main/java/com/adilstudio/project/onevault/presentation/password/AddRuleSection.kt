package com.adilstudio.project.onevault.presentation.password

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRuleSection(
    onAddRule: (PasswordRule) -> Unit,
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
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Password Rules",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Add rules to automatically generate passwords based on service name and user account",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Rule")
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
    onAddRule: (PasswordRule) -> Unit
) {
    var selectedRuleType by remember { mutableStateOf(RuleType.SERVICE_NAME) }
    var length by remember { mutableStateOf("3") }
    var selectedCasing by remember { mutableStateOf(Casing.LOWER) }
    var fixedString by remember { mutableStateOf("") }
    var showRuleTypeDropdown by remember { mutableStateOf(false) }
    var showCasingDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Password Rule") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Rule type selection
                ExposedDropdownMenuBox(
                    expanded = showRuleTypeDropdown,
                    onExpandedChange = { showRuleTypeDropdown = !showRuleTypeDropdown }
                ) {
                    OutlinedTextField(
                        value = selectedRuleType.displayName,
                        onValueChange = {},
                        label = { Text("Rule Type") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRuleTypeDropdown)
                        }
                    )

                    ExposedDropdownMenu(
                        expanded = showRuleTypeDropdown,
                        onDismissRequest = { showRuleTypeDropdown = false }
                    ) {
                        RuleType.values().forEach { ruleType ->
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
                            label = { Text("Number of characters") },
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
                                label = { Text("Text Casing") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCasingDropdown)
                                }
                            )

                            ExposedDropdownMenu(
                                expanded = showCasingDropdown,
                                onDismissRequest = { showCasingDropdown = false }
                            ) {
                                Casing.values().forEach { casing ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(casing.name.lowercase().replaceFirstChar { it.uppercase() })
                                                Text(
                                                    text = when (casing) {
                                                        Casing.LOWER -> "lowercase text"
                                                        Casing.UPPER -> "UPPERCASE TEXT"
                                                        Casing.CAPITALIZE -> "Capitalize first letter"
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
                            label = { Text("Fixed String") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("e.g., #123, @, !") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val rule = when (selectedRuleType) {
                        RuleType.SERVICE_NAME -> PasswordRule.FromServiceName(
                            length = length.toIntOrNull() ?: 3,
                            casing = selectedCasing
                        )
                        RuleType.USER_ACCOUNT -> PasswordRule.FromUserName(
                            length = length.toIntOrNull() ?: 3,
                            casing = selectedCasing
                        )
                        RuleType.FIXED_STRING -> PasswordRule.FixedString(
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
                Text("Add Rule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

enum class RuleType(val displayName: String, val description: String) {
    SERVICE_NAME("From Service Name", "Use characters from the service name"),
    USER_ACCOUNT("From User Account", "Use characters from the user account"),
    FIXED_STRING("Fixed String", "Add a fixed string or symbol")
}
