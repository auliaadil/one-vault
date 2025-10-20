package com.adilstudio.project.onevault.presentation.transaction.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.core.util.RupiahFormatter
import com.adilstudio.project.onevault.domain.model.Account
import com.adilstudio.project.onevault.presentation.MainViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAccountDialog(
    account: Account? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, amount: Double, description: String) -> Unit,
    mainViewModel: MainViewModel = koinViewModel(),
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

    val mainViewModel: MainViewModel = koinViewModel()
    val savedMessage = stringResource(R.string.account_saved_success)
    val updatedMessage = stringResource(R.string.account_updated_success)
    var showSuccess by remember { mutableStateOf(false) }

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
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_large))
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.account_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.account_name_placeholder)) }
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
                    placeholder = { Text(stringResource(R.string.balance_placeholder)) },
                    supportingText = { Text(stringResource(R.string.balance_hint)) }
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.account_description)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(stringResource(R.string.description_placeholder)) },
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(name, amountValue.toDouble(), description)
                    showSuccess = true
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

    if (showSuccess) {
        LaunchedEffect(showSuccess) {
            val message = if (account == null) savedMessage else updatedMessage
            mainViewModel.showSnackbar(message)
            showSuccess = false
        }
    }
}
