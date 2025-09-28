package com.adilstudio.project.onevault.presentation.credential.credentialform

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RuleItem(
    rule: CredentialRule,
    isDragging: Boolean = false,
    onRuleUpdate: (CredentialRule) -> Unit,
    onRuleDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(
                if (isDragging) {
                    Modifier
                        .shadow(8.dp, RoundedCornerShape(8.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f))
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Drag handle
            Icon(
                imageVector = Icons.Default.DragHandle,
                contentDescription = stringResource(R.string.drag_handle),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Rule description
            Column(modifier = Modifier.weight(1f)) {
                when (rule) {
                    is CredentialRule.FromServiceName -> {
                        Text(
                            text = "Service Name",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${rule.length} chars, ${rule.casing.name.lowercase()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    is CredentialRule.FromUserName -> {
                        Text(
                            text = "User Account",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${rule.length} chars, ${rule.casing.name.lowercase()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    is CredentialRule.FixedString -> {
                        Text(
                            text = "Fixed String",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "\"${rule.value}\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Action buttons
            Row {
                IconButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit_rule)
                    )
                }

                IconButton(
                    onClick = { onRuleDelete(rule.id) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_rule)
                    )
                }
            }
        }
    }

    // Edit dialog
    if (showEditDialog) {
        EditRuleDialog(
            rule = rule,
            onDismiss = { showEditDialog = false },
            onSave = { updatedRule ->
                onRuleUpdate(updatedRule)
                showEditDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRuleDialog(
    rule: CredentialRule,
    onDismiss: () -> Unit,
    onSave: (CredentialRule) -> Unit
) {
    var length by remember {
        mutableStateOf(
            when (rule) {
                is CredentialRule.FromServiceName -> rule.length.toString()
                is CredentialRule.FromUserName -> rule.length.toString()
                else -> "3"
            }
        )
    }
    var selectedCasing by remember {
        mutableStateOf(
            when (rule) {
                is CredentialRule.FromServiceName -> rule.casing
                is CredentialRule.FromUserName -> rule.casing
                else -> Casing.LOWER
            }
        )
    }
    var fixedString by remember {
        mutableStateOf(
            when (rule) {
                is CredentialRule.FixedString -> rule.value
                else -> ""
            }
        )
    }
    var showCasingDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = when (rule) {
                    is CredentialRule.FromServiceName -> "Edit Service Name Rule"
                    is CredentialRule.FromUserName -> "Edit User Account Rule"
                    is CredentialRule.FixedString -> "Edit Fixed String Rule"
                }
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (rule) {
                    is CredentialRule.FromServiceName, is CredentialRule.FromUserName -> {
                        OutlinedTextField(
                            value = length,
                            onValueChange = { newLength ->
                                if (newLength.all { it.isDigit() } && newLength.length <= 2) {
                                    length = newLength
                                }
                            },
                            label = { Text("Number of characters") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        ExposedDropdownMenuBox(
                            expanded = showCasingDropdown,
                            onExpandedChange = { showCasingDropdown = !showCasingDropdown }
                        ) {
                            OutlinedTextField(
                                value = selectedCasing.name.lowercase().replaceFirstChar { it.uppercase() },
                                onValueChange = {},
                                label = { Text("Casing") },
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
                                            Text(casing.name.lowercase().replaceFirstChar { it.uppercase() })
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
                    is CredentialRule.FixedString -> {
                        OutlinedTextField(
                            value = fixedString,
                            onValueChange = { fixedString = it },
                            label = { Text("Fixed string") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val updatedRule = when (rule) {
                        is CredentialRule.FromServiceName -> rule.copy(
                            length = length.toIntOrNull() ?: 3,
                            casing = selectedCasing
                        )
                        is CredentialRule.FromUserName -> rule.copy(
                            length = length.toIntOrNull() ?: 3,
                            casing = selectedCasing
                        )
                        is CredentialRule.FixedString -> rule.copy(
                            value = fixedString
                        )
                    }
                    onSave(updatedRule)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
