package com.adilstudio.project.onevault.presentation.splitbill.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adilstudio.project.onevault.domain.model.SplitItem
import com.adilstudio.project.onevault.domain.util.FeatureFlag
import com.adilstudio.project.onevault.presentation.splitbill.SplitBillViewModel
import com.adilstudio.project.onevault.core.util.DateUtil
import com.adilstudio.project.onevault.core.util.RupiahFormatter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupSplitBillStep(
    viewModel: SplitBillViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = if (FeatureFlag.isOcrSplitBillEnabled()) "Review and Setup" else "Setup Split Bill",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Basic Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = viewModel.title,
                        onValueChange = { viewModel.updateTitle(it) },
                        label = { Text("Title") },
                        placeholder = { Text("Enter bill title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = viewModel.merchant,
                        onValueChange = { viewModel.updateMerchant(it) },
                        label = { Text("Merchant") },
                        placeholder = { Text("Enter merchant name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Date picker field using DateUtil
                    val localDate = DateUtil.isoStringToLocalDate(viewModel.date)
                    val displayDate = localDate?.let { DateUtil.formatDateForDisplay(it) } ?: viewModel.date
                    OutlinedTextField(
                        value = displayDate,
                        onValueChange = { },
                        label = { Text("Date") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Tax field with % suffix
                        OutlinedTextField(
                            value = if (viewModel.tax == 0.0) "" else viewModel.tax.toString(),
                            onValueChange = {
                                val value = it.replace("%", "").toDoubleOrNull() ?: 0.0
                                viewModel.updateTax(value)
                            },
                            label = { Text("Tax") },
                            suffix = { Text("%") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )

                        // Service Fee field with % suffix
                        OutlinedTextField(
                            value = if (viewModel.serviceFee == 0.0) "" else viewModel.serviceFee.toString(),
                            onValueChange = {
                                val value = it.replace("%", "").toDoubleOrNull() ?: 0.0
                                viewModel.updateServiceFee(value)
                            },
                            label = { Text("Service Fee") },
                            suffix = { Text("%") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Suggested items from OCR - only show when OCR is enabled
        if (FeatureFlag.isOcrSplitBillEnabled() && uiState.suggestedItems.isNotEmpty()) {
            item {
                Text(
                    text = "Suggested Items from Receipt",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tap to add items to your split bill",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(uiState.suggestedItems) { suggestedItem ->
                SuggestedItemCard(
                    item = suggestedItem,
                    onAddItem = { viewModel.addSuggestedItem(it) },
                    onRemove = { viewModel.removeSuggestedItem(it.id) },
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Items in Split Bill",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = { viewModel.addItem() }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Item")
                }
            }
        }

        if (uiState.items.isEmpty()) {
            item {
                Card {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No items added yet",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (FeatureFlag.isOcrSplitBillEnabled()) {
                                    "Add items from suggestions above or manually"
                                } else {
                                    "Tap the + button to add items manually"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else {
            items(uiState.items) { item ->
                ItemCard(
                    item = item,
                    onDescriptionChange = { viewModel.updateItemDescription(item.id, it) },
                    onPriceChange = { viewModel.updateItemPrice(item.id, it) },
                    onRemove = { viewModel.removeItem(item.id) },
                )
            }
        }
    }

    // Date picker dialog
    if (showDatePicker) {
        val initialDate = LocalDate.now()
        val initialMillis = initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        viewModel.updateDate(DateUtil.localDateToIsoString(selectedDate))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemCard(
    item: SplitItem,
    onDescriptionChange: (String) -> Unit,
    onPriceChange: (Double) -> Unit,
    onRemove: () -> Unit,
) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = item.description,
                        onValueChange = onDescriptionChange,
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = if (item.price == 0.0) "" else {
                            // Remove currency formatting for editing
                            item.price.toInt().toString()
                        },
                        onValueChange = {
                            val cleanValue = it.replace(Regex("[^\\d]"), "")
                            val value = cleanValue.toDoubleOrNull() ?: 0.0
                            onPriceChange(value)
                        },
                        label = { Text("Price per item") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = {
                            if (item.price > 0) {
                                Text(
                                    text = RupiahFormatter.formatWithRupiahPrefix(item.price.toLong()),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }

                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove Item")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestedItemCard(
    item: SplitItem,
    onAddItem: (SplitItem) -> Unit,
    onRemove: (SplitItem) -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = RupiahFormatter.formatRupiahDisplay(item.price.toLong()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row {
                IconButton(onClick = { onAddItem(item) }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add to split bill",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { onRemove(item) }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove suggestion"
                    )
                }
            }
        }
    }
}
