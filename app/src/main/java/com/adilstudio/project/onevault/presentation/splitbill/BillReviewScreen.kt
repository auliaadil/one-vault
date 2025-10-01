package com.adilstudio.project.onevault.presentation.splitbill

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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.core.util.RupiahFormatter
import com.adilstudio.project.onevault.domain.model.BillItem
import com.adilstudio.project.onevault.presentation.component.GenericScreen
import com.adilstudio.project.onevault.ui.theme.OneVaultTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun BillReviewScreen(
    splitBillViewModel: SplitBillViewModel = koinViewModel(),
    onProceedToParticipants: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val billItems by splitBillViewModel.billItems.collectAsState()
    val taxPercentage by splitBillViewModel.taxPercentage.collectAsState()
    val servicePercentage by splitBillViewModel.servicePercentage.collectAsState()

    var showAddItemDialog by remember { mutableStateOf(false) }

    val subtotal = billItems.sumOf { it.subtotal }
    val taxAmount = subtotal * (taxPercentage / 100.0)
    val serviceAmount = subtotal * (servicePercentage / 100.0)
    val total = subtotal + taxAmount + serviceAmount

    GenericScreen(
        title = "Review Items",
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = { showAddItemDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = dimensionResource(R.dimen.spacing_large)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
            ) {
                items(billItems) { item ->
                    BillItemCard(
                        item = item,
                        onEdit = { editedItem ->
                            splitBillViewModel.updateBillItem(editedItem)
                        },
                        onDelete = { itemId ->
                            splitBillViewModel.deleteBillItem(itemId)
                        }
                    )
                }

                item {
                    TaxServiceCard(
                        taxPercentage = taxPercentage,
                        servicePercentage = servicePercentage,
                        onTaxChange = { splitBillViewModel.updateTaxPercentage(it) },
                        onServiceChange = { splitBillViewModel.updateServicePercentage(it) }
                    )
                }
            }

            // Summary and Continue
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.spacing_large))
            ) {
                Column(
                    modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large))
                ) {
                    Text(
                        text = "Bill Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal:")
                        Text(RupiahFormatter.formatWithRupiahPrefix(subtotal.toLong()))
                    }

                    if (taxAmount > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tax (${taxPercentage}%):")
                            Text(RupiahFormatter.formatWithRupiahPrefix(taxAmount.toLong()))
                        }
                    }

                    if (serviceAmount > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Service (${servicePercentage}%):")
                            Text(RupiahFormatter.formatWithRupiahPrefix(serviceAmount.toLong()))
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = dimensionResource(R.dimen.spacing_small)))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = RupiahFormatter.formatWithRupiahPrefix(total.toLong()),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))

                    Button(
                        onClick = onProceedToParticipants,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = billItems.isNotEmpty()
                    ) {
                        Text("Continue to Participants")
                    }
                }
            }
        }
    }

    if (showAddItemDialog) {
        AddItemDialog(
            onDismiss = { showAddItemDialog = false },
            onAdd = { item ->
                splitBillViewModel.addBillItem(item)
                showAddItemDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillItemCard(
    item: BillItem,
    onEdit: (BillItem) -> Unit,
    onDelete: (Long) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacing_medium)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${RupiahFormatter.formatWithRupiahPrefix(item.price.toLong())} x ${item.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = RupiahFormatter.formatWithRupiahPrefix(item.subtotal.toLong()),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Row {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_small))
                    )
                }
                IconButton(onClick = { onDelete(item.id) }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_small)),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showEditDialog) {
        EditItemDialog(
            item = item,
            onDismiss = { showEditDialog = false },
            onSave = { editedItem ->
                onEdit(editedItem)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun TaxServiceCard(
    taxPercentage: Double,
    servicePercentage: Double,
    onTaxChange: (Double) -> Unit,
    onServiceChange: (Double) -> Unit
) {
    var taxText by remember { mutableStateOf(taxPercentage.toString()) }
    var serviceText by remember { mutableStateOf(servicePercentage.toString()) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_medium))
        ) {
            Text(
                text = "Tax & Service",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
            ) {
                OutlinedTextField(
                    value = taxText,
                    onValueChange = {
                        taxText = it
                        it.toDoubleOrNull()?.let { value -> onTaxChange(value) }
                    },
                    label = { Text("Tax (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = serviceText,
                    onValueChange = {
                        serviceText = it
                        it.toDoubleOrNull()?.let { value -> onServiceChange(value) }
                    },
                    label = { Text("Service (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onAdd: (BillItem) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Item") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val priceValue = price.toDoubleOrNull() ?: 0.0
                    val quantityValue = quantity.toIntOrNull() ?: 1
                    if (name.isNotBlank() && priceValue > 0) {
                        onAdd(BillItem(
                            name = name,
                            price = priceValue,
                            quantity = quantityValue
                        ))
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditItemDialog(
    item: BillItem,
    onDismiss: () -> Unit,
    onSave: (BillItem) -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    var price by remember { mutableStateOf(item.price.toString()) }
    var quantity by remember { mutableStateOf(item.quantity.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Item") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val priceValue = price.toDoubleOrNull() ?: item.price
                    val quantityValue = quantity.toIntOrNull() ?: item.quantity
                    onSave(item.copy(
                        name = name,
                        price = priceValue,
                        quantity = quantityValue
                    ))
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

@Preview(showBackground = true)
@Composable
fun BillReviewScreenPreview() {
    OneVaultTheme {
        BillReviewScreen()
    }
}
