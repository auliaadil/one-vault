package com.adilstudio.project.onevault.presentation.bill

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.adilstudio.project.onevault.R

data class ScannedBillData(
    val title: String = "",
    val amount: String = "",
    val vendor: String = ""
)

@Composable
fun TextSelectionDialog(
    scannedTexts: List<String>,
    onTextSelected: (title: String, amount: String, vendor: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTitle by remember { mutableStateOf("") }
    var selectedAmount by remember { mutableStateOf("") }
    var selectedVendor by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_scanned_text)) },
        text = {
            Column {
                Text(
                    stringResource(R.string.tap_text_assign),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

                // Show current selections
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(dimensionResource(R.dimen.spacing_small))) {
                        Text(
                            stringResource(R.string.selected_colon),
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            stringResource(
                                R.string.title_field,
                                selectedTitle.ifEmpty { stringResource(R.string.none) }),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            stringResource(
                                R.string.amount_field,
                                selectedAmount.ifEmpty { stringResource(R.string.none) }),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            stringResource(
                                R.string.vendor_field,
                                selectedVendor.ifEmpty { stringResource(R.string.none) }),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

                LazyColumn(modifier = Modifier.height(dimensionResource(R.dimen.scanner_text_selection_height))) {
                    itemsIndexed(scannedTexts) { index, text ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = dimensionResource(R.dimen.spacing_xs)),
                            colors = CardDefaults.cardColors(
                                containerColor = when (text) {
                                    selectedTitle -> MaterialTheme.colorScheme.primaryContainer
                                    selectedAmount -> MaterialTheme.colorScheme.secondaryContainer
                                    selectedVendor -> MaterialTheme.colorScheme.tertiaryContainer
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            )
                        ) {
                            Column(modifier = Modifier.padding(dimensionResource(R.dimen.scanner_item_padding))) {
                                Text(text, style = MaterialTheme.typography.bodyMedium)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.scanner_button_spacing))
                                ) {
                                    Button(
                                        onClick = { selectedTitle = text },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectedTitle == text)
                                                MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.outline
                                        )
                                    ) {
                                        Text(
                                            stringResource(R.string.title),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }

                                    Button(
                                        onClick = { selectedAmount = text },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectedAmount == text)
                                                MaterialTheme.colorScheme.secondary
                                            else MaterialTheme.colorScheme.outline
                                        )
                                    ) {
                                        Text(
                                            stringResource(R.string.amount),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }

                                    Button(
                                        onClick = { selectedVendor = text },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectedVendor == text)
                                                MaterialTheme.colorScheme.tertiary
                                            else MaterialTheme.colorScheme.outline
                                        )
                                    ) {
                                        Text(
                                            stringResource(R.string.vendor),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onTextSelected(selectedTitle, selectedAmount, selectedVendor)
                }
            ) {
                Text(stringResource(R.string.apply))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
