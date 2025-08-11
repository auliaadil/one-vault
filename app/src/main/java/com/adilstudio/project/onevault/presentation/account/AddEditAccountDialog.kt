package com.adilstudio.project.onevault.presentation.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.core.util.RupiahFormatter
import com.adilstudio.project.onevault.domain.model.Account

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAccountDialog(
    account: Account? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, amount: Double, description: String) -> Unit
) {
    var name by remember { mutableStateOf(account?.name ?: "") }
    var amountValue by remember { mutableStateOf(account?.amount?.toLong() ?: 0L) }
    var amountDisplay by remember {
        mutableStateOf(
            if (account?.amount != null)
                RupiahFormatter.formatRupiahDisplay(account.amount.toLong())
            else ""
        )
    }
    var description by remember { mutableStateOf(account?.description ?: "") }

    val isEditing = account != null
    val isValid = name.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) stringResource(R.string.edit_account) else stringResource(R.string.add_account)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.account_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g., BCA Savings, Mandiri Current") }
                )

                OutlinedTextField(
                    value = amountDisplay,
                    onValueChange = { newValue ->
                        // Handle negative values by checking for minus sign
                        val cleanValue = newValue.replace(Regex("[^0-9-]"), "")

                        if (cleanValue.isNotEmpty()) {
                            val isNegative = cleanValue.startsWith("-")
                            val digitsOnly = cleanValue.replace("-", "")

                            if (digitsOnly.length <= 15) { // Reasonable limit
                                val longValue = digitsOnly.toLongOrNull() ?: 0L
                                val finalValue = if (isNegative) -longValue else longValue
                                amountValue = finalValue
                                amountDisplay = RupiahFormatter.formatRupiahDisplay(finalValue)
                            }
                        } else {
                            amountValue = 0L
                            amountDisplay = ""
                        }
                    },
                    label = { Text(stringResource(R.string.current_balance)) },
                    leadingIcon = {
                        Text(
                            text = stringResource(R.string.rupiah_prefix),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("0 (can be negative)") },
                    supportingText = { Text("Enter current account balance. Use minus sign for debt/credit accounts.") }
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g., Primary savings account") },
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(name, amountValue.toDouble(), description)
                },
                enabled = isValid
            ) {
                Text(if (isEditing) stringResource(R.string.update) else stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
