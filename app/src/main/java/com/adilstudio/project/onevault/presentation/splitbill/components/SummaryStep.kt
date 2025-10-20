package com.adilstudio.project.onevault.presentation.splitbill.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.core.util.DateUtil
import com.adilstudio.project.onevault.core.util.RupiahFormatter
import com.adilstudio.project.onevault.domain.model.SplitBill
import com.adilstudio.project.onevault.domain.model.SplitParticipant
import com.adilstudio.project.onevault.presentation.splitbill.SplitBillViewModel
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryStep(
    viewModel: SplitBillViewModel,
    calculatedParticipants: List<SplitParticipant>,
    splitBill: SplitBill?,
    validationErrors: List<String>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Split Bill Summary",
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
                    val displayDate = localDate?.let { DateUtil.formatDateForDisplay(it) } ?: viewModel.date
                    DetailRow("Date:", displayDate)

                    val totalPrice = viewModel.uiState.collectAsState().value.items.sumOf { it.price }
                    val taxPercent = viewModel.tax
                    val serviceFeePercent = viewModel.serviceFee
                    val taxAmount = totalPrice * (taxPercent / 100.0)
                    val serviceFeeAmount = totalPrice * (serviceFeePercent / 100.0)

                    DetailRow("Total Base Amount:", RupiahFormatter.formatWithRupiahPrefix(totalPrice.toLong()))
                    DetailRow("Tax:", "$taxPercent% (" + RupiahFormatter.formatWithRupiahPrefix(taxAmount.toLong()) + ")")
                    DetailRow("Service Fee:", "$serviceFeePercent% (" + RupiahFormatter.formatWithRupiahPrefix(serviceFeeAmount.toLong()) + ")")

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Calculate total amount including tax and service fee for each participant
                    val totalAmount = calculatedParticipants.sumOf { it.shareAmount }
                    DetailRow(
                        label = "Total Amount:",
                        value = RupiahFormatter.formatWithRupiahPrefix(totalAmount.toLong()),
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
                        viewModel.exportToTransaction(participant.name)
                    },
                    splitBill = splitBill
                )
            }
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
    splitBill: SplitBill?
) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
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
                            text = RupiahFormatter.formatWithRupiahPrefix(participant.shareAmount.toLong()),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (splitBill != null) {
                    OutlinedButton(
                        onClick = onExportToTransaction,
                        modifier = Modifier.size(width = 120.dp, height = 36.dp)
                    ) {
                        Text(
                            text = "Export",
                            style = MaterialTheme.typography.bodySmall
                        )
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
