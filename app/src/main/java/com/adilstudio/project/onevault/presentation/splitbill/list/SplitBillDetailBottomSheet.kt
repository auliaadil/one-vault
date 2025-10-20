package com.adilstudio.project.onevault.presentation.splitbill.list

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.core.util.DateUtil
import com.adilstudio.project.onevault.core.util.RupiahFormatter
import com.adilstudio.project.onevault.core.util.ShareImageGenerator
import com.adilstudio.project.onevault.domain.model.SplitBill
import com.adilstudio.project.onevault.domain.model.SplitParticipant
import com.adilstudio.project.onevault.domain.usecase.ExportParticipantToTransactionUseCase
import com.adilstudio.project.onevault.presentation.component.GenericBottomSheet
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitBillDetailBottomSheet(
    splitBill: SplitBill,
    onDismiss: () -> Unit,
    onDelete: (Long) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val exportUseCase: ExportParticipantToTransactionUseCase = koinInject()
    var showParticipantDetail by remember { mutableStateOf<SplitParticipant?>(null) }
    var isSharing by remember { mutableStateOf(false) }

    GenericBottomSheet(
        title = "Split Bill Details",
        onDelete = {},
        deleteDialogText = "Are you sure you want to delete \"${splitBill.title}\"? This action cannot be undone.",
        onDeleteConfirmed = {
            onDelete(splitBill.id)
            onDismiss()
        },
        onDismiss = onDismiss
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_large))
        ) {
            // Bill Information
            item {
                Card {
                    Column(modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large))) {
                        Text(
                            text = "Bill Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

                        DetailRow("Title", splitBill.title)

                        if (!splitBill.merchant.isNullOrBlank()) {
                            DetailRow("Merchant", splitBill.merchant)
                        }

                        val localDate = DateUtil.isoStringToLocalDate(splitBill.date)
                        val displayDate = localDate?.let { DateUtil.formatDateForDisplay(it) } ?: splitBill.date
                        DetailRow("Date", displayDate)

                        DetailRow(
                            "Total Amount",
                            RupiahFormatter.formatWithRupiahPrefix(splitBill.totalAmount.toLong())
                        )

                        if (splitBill.tax > 0) {
                            DetailRow("Tax", "${splitBill.tax}%")
                        }

                        if (splitBill.serviceFee > 0) {
                            DetailRow("Service Fee", "${splitBill.serviceFee}%")
                        }
                    }
                }
            }

            // Items Section
            if (splitBill.items.isNotEmpty()) {
                item {
                    Text(
                        text = "Items (${splitBill.items.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(splitBill.items) { item ->
                    Card {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensionResource(R.dimen.spacing_large)),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                val totalQuantity = item.assignedQuantities.values.sum()
                                if (totalQuantity > 0) {
                                    Text(
                                        text = "Qty: $totalQuantity",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Text(
                                text = RupiahFormatter.formatWithRupiahPrefix(item.price.toLong()),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Participants Section
            if (splitBill.participants.isNotEmpty()) {
                item {
                    Text(
                        text = "Participants (${splitBill.participants.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(splitBill.participants) { participant ->
                    Card(
                        onClick = { showParticipantDetail = participant }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensionResource(R.dimen.spacing_large)),
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
                                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
                                Column {
                                    Text(
                                        text = participant.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if (!participant.note.isNullOrBlank()) {
                                        Text(
                                            text = participant.note,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = RupiahFormatter.formatWithRupiahPrefix(participant.shareAmount.toLong()),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Share button
            item {
                Button(
                    onClick = {
                        if (!isSharing) {
                            isSharing = true
                            scope.launch {
                                val shareImageGenerator = ShareImageGenerator(context)
                                val imageUri = shareImageGenerator.generateCompleteSplitBillImage(
                                    billTitle = splitBill.title,
                                    merchant = splitBill.merchant ?: "",
                                    date = splitBill.date,
                                    items = splitBill.items,
                                    participants = splitBill.participants,
                                    taxPercent = splitBill.tax,
                                    serviceFeePercent = splitBill.serviceFee,
                                    totalAmount = splitBill.totalAmount
                                )

                                if (imageUri != null) {
                                    val shareIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        type = "image/png"
                                        putExtra(Intent.EXTRA_STREAM, imageUri)
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "Split Bill: ${splitBill.title} - Total: ${
                                                RupiahFormatter.formatWithRupiahPrefix(splitBill.totalAmount.toLong())
                                            }"
                                        )
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(
                                        Intent.createChooser(shareIntent, "Share split bill")
                                    )
                                }
                                isSharing = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isSharing
                ) {
                    if (isSharing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generating...")
                    } else {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share Split Bill")
                    }
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
            }
        }
    }

    // Show participant detail dialog
    showParticipantDetail?.let { participant ->
        ParticipantDetailDialog(
            participant = participant,
            splitBill = splitBill,
            onDismiss = { showParticipantDetail = null },
            onExport = {
                scope.launch {
                    val success = exportUseCase(splitBill, participant)
                    if (success) {
                        // Show success message
                        showParticipantDetail = null
                    }
                }
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.spacing_xs)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ParticipantDetailDialog(
    participant: SplitParticipant,
    splitBill: SplitBill,
    onDismiss: () -> Unit,
    onExport: () -> Unit
) {
    val context = LocalContext.current
    var isSharing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(participant.name)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Share Amount",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = RupiahFormatter.formatWithRupiahPrefix(participant.shareAmount.toLong()),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (!participant.note.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Note",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = participant.note,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Assigned items
                val assignedItems = splitBill.items.filter { item ->
                    item.assignedQuantities.containsKey(participant.name) &&
                            item.assignedQuantities[participant.name]!! > 0
                }

                if (assignedItems.isNotEmpty()) {
                    Text(
                        text = "Assigned Items",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    assignedItems.forEach { item ->
                        val quantity = item.assignedQuantities[participant.name] ?: 0
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${item.description} (x$quantity)",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = RupiahFormatter.formatWithRupiahPrefix((item.price * quantity).toLong()),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        if (!isSharing) {
                            isSharing = true
                            scope.launch {
                                val assignedItems = splitBill.items.filter { item ->
                                    item.assignedQuantities.containsKey(participant.name) &&
                                            item.assignedQuantities[participant.name]!! > 0
                                }.map { item ->
                                    Pair(item, item.assignedQuantities[participant.name] ?: 0)
                                }

                                val shareImageGenerator = ShareImageGenerator(context)
                                val imageUri = shareImageGenerator.generateParticipantShareImage(
                                    participant = participant,
                                    assignedItems = assignedItems,
                                    taxPercent = splitBill.tax,
                                    serviceFeePercent = splitBill.serviceFee,
                                    billTitle = splitBill.title,
                                    merchant = splitBill.merchant ?: "",
                                    date = splitBill.date
                                )

                                if (imageUri != null) {
                                    val shareIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        type = "image/png"
                                        putExtra(Intent.EXTRA_STREAM, imageUri)
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "Split bill share for ${participant.name}: ${
                                                RupiahFormatter.formatWithRupiahPrefix(participant.shareAmount.toLong())
                                            }"
                                        )
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(
                                        Intent.createChooser(shareIntent, "Share participant details")
                                    )
                                }
                                isSharing = false
                            }
                        }
                    },
                    enabled = !isSharing
                ) {
                    if (isSharing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
                Button(onClick = onExport) {
                    Text("Save to Transaction")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
