package com.adilstudio.project.onevault.presentation.splitbill.form.components

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.core.util.CurrencyFormatter
import com.adilstudio.project.onevault.core.util.DateUtil
import com.adilstudio.project.onevault.core.util.ShareImageGenerator
import com.adilstudio.project.onevault.domain.model.Currency
import com.adilstudio.project.onevault.domain.model.SplitBill
import com.adilstudio.project.onevault.domain.model.SplitItem
import com.adilstudio.project.onevault.domain.model.SplitParticipant
import com.adilstudio.project.onevault.presentation.MainViewModel
import com.adilstudio.project.onevault.presentation.splitbill.form.SplitBillFormViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryStep(
    viewModel: SplitBillFormViewModel,
    calculatedParticipants: List<SplitParticipant>,
    splitBill: SplitBill?,
    validationErrors: List<String>,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = koinViewModel()
) {
    var selectedParticipant by remember { mutableStateOf<SplitParticipant?>(null) }
    val uiState by viewModel.uiState.collectAsState()
    val currency = Currency.current

    // Show success message via MainViewModel when export succeeds
    uiState.exportSuccessMessage?.let { message ->
        LaunchedEffect(message) {
            mainViewModel.showSnackbar(message)
            viewModel.clearExportSuccess()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = modifier.padding(vertical = dimensionResource(R.dimen.spacing_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_large))
        ) {
            item {
                Text(
                    text = stringResource(R.string.summary_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // Validation errors
            if (validationErrors.isNotEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Validation Errors",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            validationErrors.forEach { error ->
                                Text(
                                    text = "• $error",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }

            // Bill details
            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Bill Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        DetailRow("Title:", viewModel.title)
                        DetailRow("Merchant:", viewModel.merchant.ifEmpty { "Not specified" })

                        // Format date using DateUtil (ISO string to LocalDate to display format)
                        val localDate = DateUtil.isoStringToLocalDate(viewModel.date)
                        val displayDate =
                            localDate?.let { DateUtil.formatDateForDisplay(it) } ?: viewModel.date
                        DetailRow("Date:", displayDate)

                        // Calculate total base amount from all assigned items
                        val totalPrice = uiState.items.sumOf { item ->
                            val totalQuantity = item.assignedQuantities.values.sum()
                            item.price * totalQuantity
                        }
                        val taxPercent = viewModel.tax
                        val serviceFeePercent = viewModel.serviceFee
                        val taxAmount = totalPrice * (taxPercent / 100.0)
                        val serviceFeeAmount = totalPrice * (serviceFeePercent / 100.0)

                        DetailRow(
                            "Total Base Amount:",
                            CurrencyFormatter.formatWithPrefix(totalPrice.toLong(), currency)
                        )
                        DetailRow(
                            "Tax:",
                            "$taxPercent% (" + CurrencyFormatter.formatWithPrefix(
                                taxAmount.toLong(),
                                currency
                            ) + ")"
                        )
                        DetailRow(
                            "Service Fee:",
                            "$serviceFeePercent% (" + CurrencyFormatter.formatWithPrefix(
                                serviceFeeAmount.toLong(), currency
                            ) + ")"
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        // Calculate total amount including tax and service fee for each participant
                        val totalAmount = calculatedParticipants.sumOf { it.shareAmount }
                        DetailRow(
                            label = "Total Amount:",
                            value = CurrencyFormatter.formatWithPrefix(
                                totalAmount.toLong(),
                                currency
                            ),
                            isTotal = true
                        )
                    }
                }
            }

            // Participant shares
            item {
                Text(
                    text = "Participant Shares",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (calculatedParticipants.isEmpty()) {
                item {
                    Card {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No participants to calculate",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(calculatedParticipants) { participant ->
                    ParticipantShareCard(
                        participant = participant,
                        onExportToTransaction = {
                            viewModel.exportCurrentSplitBillToTransaction(participant)
                        },
                        onClick = { selectedParticipant = participant },
                        splitBill = splitBill
                    )
                }
            }
        }

        // Participant Detail Bottom Sheet
        selectedParticipant?.let { participant ->
            ParticipantDetailBottomSheet(
                participant = participant,
                billTitle = viewModel.title,
                merchant = viewModel.merchant,
                date = viewModel.date,
                items = uiState.items,
                taxPercent = viewModel.tax,
                serviceFeePercent = viewModel.serviceFee,
                onDismiss = { selectedParticipant = null },
                onExportToTransaction = {
                    viewModel.exportCurrentSplitBillToTransaction(participant)
                    selectedParticipant = null
                }
            )
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            style = if (isTotal) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ParticipantShareCard(
    participant: SplitParticipant,
    onExportToTransaction: () -> Unit,
    onClick: () -> Unit,
    splitBill: SplitBill?
) {
    val currency = Currency.current

    Card(
        modifier = Modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = participant.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = CurrencyFormatter.formatWithPrefix(
                                participant.shareAmount.toLong(),
                                currency
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row {
                    IconButton(onClick = onClick) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "View details",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (splitBill != null) {
                        OutlinedButton(
                            onClick = onExportToTransaction,
                            modifier = Modifier.size(width = 100.dp, height = 36.dp)
                        ) {
                            Text(
                                text = "Export",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            if (!participant.note.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = participant.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantDetailBottomSheet(
    participant: SplitParticipant,
    billTitle: String,
    merchant: String,
    date: String,
    items: List<SplitItem>,
    taxPercent: Double,
    serviceFeePercent: Double,
    onDismiss: () -> Unit,
    onExportToTransaction: () -> Unit
) {
    val currency = Currency.current
    val context = LocalContext.current
    var showExportDialog by remember { mutableStateOf(false) }
    var isSharing by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = participant.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Participant Details",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Share Summary
            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Share Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        DetailRow(
                            label = "Total Share:",
                            value = CurrencyFormatter.formatWithPrefix(
                                participant.shareAmount.toLong(),
                                currency
                            ),
                            isTotal = true
                        )

                        if (!participant.note.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Note: ${participant.note}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Assigned Items
            item {
                Text(
                    text = "Assigned Items",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Filter items that are assigned to this participant
            val assignedItems = items.filter { item ->
                item.assignedQuantities.containsKey(participant.name) &&
                        item.assignedQuantities[participant.name]!! > 0
            }

            if (assignedItems.isEmpty()) {
                item {
                    Card {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No items assigned to ${participant.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(assignedItems) { item ->
                    val quantity = item.assignedQuantities[participant.name] ?: 0
                    val itemTotal = item.price * quantity

                    Card {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.description,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Qty: $quantity × ${
                                            CurrencyFormatter.formatWithPrefix(
                                                item.price.toLong(), currency
                                            )
                                        }",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = CurrencyFormatter.formatWithPrefix(
                                        itemTotal.toLong(),
                                        currency
                                    ),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Breakdown Section
            item {
                Card {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Share Breakdown",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val baseAmount = assignedItems.sumOf { item ->
                            (item.assignedQuantities[participant.name] ?: 0) * item.price
                        }
                        val participantTax = baseAmount * (taxPercent / 100.0)
                        val participantServiceFee = baseAmount * (serviceFeePercent / 100.0)

                        DetailRow(
                            "Items Total:",
                            CurrencyFormatter.formatWithPrefix(baseAmount.toLong(), currency)
                        )
                        DetailRow(
                            "Tax ($taxPercent%):",
                            CurrencyFormatter.formatWithPrefix(participantTax.toLong(), currency)
                        )
                        DetailRow(
                            "Service Fee ($serviceFeePercent%):",
                            CurrencyFormatter.formatWithPrefix(
                                participantServiceFee.toLong(),
                                currency
                            )
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        DetailRow(
                            label = "Total Share:",
                            value = CurrencyFormatter.formatWithPrefix(
                                participant.shareAmount.toLong(),
                                currency
                            ),
                            isTotal = true
                        )
                    }
                    // Action buttons
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Export to Transaction button
                    Button(
                        onClick = { showExportDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.AccountBalance,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save")
                    }

                    // Share button
                    OutlinedButton(
                        onClick = {
                            if (!isSharing) {
                                isSharing = true
                                val assignedItems = items.filter { item ->
                                    item.assignedQuantities.containsKey(participant.name) &&
                                            item.assignedQuantities[participant.name]!! > 0
                                }.map { item ->
                                    Pair(item, item.assignedQuantities[participant.name] ?: 0)
                                }

                                val shareImageGenerator = ShareImageGenerator(context)
                                val imageUri = shareImageGenerator.generateParticipantShareImage(
                                    participant = participant,
                                    assignedItems = assignedItems,
                                    taxPercent = taxPercent,
                                    serviceFeePercent = serviceFeePercent,
                                    billTitle = billTitle,
                                    merchant = merchant,
                                    date = date
                                )

                                if (imageUri != null) {
                                    val shareIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        type = "image/png"
                                        putExtra(Intent.EXTRA_STREAM, imageUri)
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "Split bill share for ${participant.name}: ${
                                                CurrencyFormatter.formatWithPrefix(
                                                    participant.shareAmount.toLong(),
                                                    currency
                                                )
                                            }"
                                        )
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(
                                        Intent.createChooser(
                                            shareIntent,
                                            "Share split bill details"
                                        )
                                    )
                                }
                                isSharing = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = !isSharing
                    ) {
                        if (isSharing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isSharing) "Generating..." else "Share")
                    }
                }
            }

            // Add bottom padding
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        // Export Confirmation Dialog
        if (showExportDialog) {
            AlertDialog(
                onDismissRequest = { showExportDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save to Transaction")
                    }
                },
                text = {
                    Column {
                        Text(
                            "This will create a new expense transaction for ${participant.name}'s share:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Amount: ${
                                CurrencyFormatter.formatWithPrefix(
                                    participant.shareAmount.toLong(),
                                    currency
                                )
                            }",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "The transaction will be added to your personal expense tracking with all the bill details included.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        showExportDialog = false
                        onExportToTransaction()
                    }) {
                        Text("Create Transaction")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExportDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}