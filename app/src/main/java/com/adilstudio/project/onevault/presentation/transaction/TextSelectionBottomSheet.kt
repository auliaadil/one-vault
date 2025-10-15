package com.adilstudio.project.onevault.presentation.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextSelectionBottomSheet(
    scannedTexts: List<String>,
    onTextSelected: (title: String, amount: String, vendor: String) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    var selectedTitle by remember { mutableStateOf("") }
    var selectedAmount by remember { mutableStateOf("") }
    var selectedVendor by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.spacing_medium))) {
            Text(
                stringResource(R.string.select_scanned_text),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                stringResource(R.string.tap_text_assign),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
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
            LazyColumn {
                itemsIndexed(scannedTexts) { index, text ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = dimensionResource(R.dimen.spacing_xs)),
                        colors = CardDefaults.cardColors(
                            containerColor = when (text) {
                                selectedTitle, selectedAmount, selectedVendor -> MaterialTheme.colorScheme.tertiaryContainer
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
                                            MaterialTheme.colorScheme.tertiary
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
                                            MaterialTheme.colorScheme.tertiary
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
                                        stringResource(R.string.merchant),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
                Button(
                    onClick = {
                        onTextSelected(selectedTitle, selectedAmount, selectedVendor)
                        coroutineScope.launch { sheetState.hide() }
                    }
                ) {
                    Text(stringResource(R.string.apply))
                }
            }
        }
    }
}
