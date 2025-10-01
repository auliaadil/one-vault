package com.adilstudio.project.onevault.presentation.splitbill

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.core.util.RupiahFormatter
import com.adilstudio.project.onevault.domain.model.*
import com.adilstudio.project.onevault.presentation.component.GenericScreen
import com.adilstudio.project.onevault.ui.theme.OneVaultTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplitResultsScreen(
    splitBillViewModel: SplitBillViewModel = koinViewModel(),
    onSave: () -> Unit = {},
    onShare: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val splitResults by splitBillViewModel.splitResults.collectAsState()
    val currentBill by splitBillViewModel.currentBill.collectAsState()
    val isLoading by splitBillViewModel.isLoading.collectAsState()
    val successMessage by splitBillViewModel.successMessage.collectAsState()

    LaunchedEffect(successMessage) {
        successMessage?.let {
            splitBillViewModel.clearMessages()
        }
    }

    GenericScreen(
        title = "Split Results",
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = onShare) {
                Icon(Icons.Default.Share, contentDescription = "Share")
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
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
            ) {
                item {
                    currentBill?.let { bill ->
                        BillSummaryCard(bill = bill)
                    }
                }

                item {
                    Text(
                        text = "Per Person Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = dimensionResource(R.dimen.spacing_small))
                    )
                }

                items(splitResults) { result ->
                    ParticipantResultCard(result = result)
                }

                item {
                    TotalSummaryCard(
                        splitResults = splitResults,
                        currentBill = currentBill
                    )
                }
            }

            // Action buttons
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.spacing_large))
            ) {
                Column(
                    modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
                ) {
                    Button(
                        onClick = {
                            splitBillViewModel.saveSplitBill()
                            onSave()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(dimensionResource(R.dimen.icon_small)),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
                        }
                        Text("Save Split Bill")
                    }

                    OutlinedButton(
                        onClick = onShare,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
                        Text("Share Results")
                    }
                }
            }
        }
    }
}

@Composable
fun BillSummaryCard(bill: SplitBill) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_medium))
        ) {
            Text(
                text = bill.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = bill.vendor,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = bill.billDate,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Split Method:")
                Text(
                    text = bill.splitMethod.name.lowercase().replace('_', ' '),
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Participants:")
                Text(
                    text = "${bill.participants.size}",
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Amount:")
                Text(
                    text = RupiahFormatter.formatWithRupiahPrefix(bill.total.toLong()),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ParticipantResultCard(result: BillSplitResult) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_medium))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = result.participant.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    result.participant.email?.let { email ->
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = RupiahFormatter.formatWithRupiahPrefix(result.total.toLong()),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

            // Breakdown
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Subtotal:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = RupiahFormatter.formatWithRupiahPrefix(result.subtotal.toLong()),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (result.taxAmount > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tax:",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = RupiahFormatter.formatWithRupiahPrefix(result.taxAmount.toLong()),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                if (result.serviceAmount > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Service:",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = RupiahFormatter.formatWithRupiahPrefix(result.serviceAmount.toLong()),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            if (result.assignedItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

                Text(
                    text = "Assigned Items:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )

                result.assignedItems.forEach { item ->
                    Text(
                        text = "• ${item.name} x${item.quantity}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = dimensionResource(R.dimen.spacing_small))
                    )
                }
            }
        }
    }
}

@Composable
fun TotalSummaryCard(
    splitResults: List<BillSplitResult>,
    currentBill: SplitBill?
) {
    val calculatedTotal = splitResults.sumOf { it.total }
    val originalTotal = currentBill?.total ?: 0.0
    val difference = originalTotal - calculatedTotal

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_medium))
        ) {
            Text(
                text = "Verification",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Original Total:")
                Text(RupiahFormatter.formatWithRupiahPrefix(originalTotal.toLong()))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Sum of Split:")
                Text(RupiahFormatter.formatWithRupiahPrefix(calculatedTotal.toLong()))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Difference:")
                Text(
                    text = RupiahFormatter.formatWithRupiahPrefix(difference.toLong()),
                    color = if (kotlin.math.abs(difference) < 1.0) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }

            if (kotlin.math.abs(difference) < 1.0) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_small))
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
                    Text(
                        text = "Split calculation verified!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplitResultsScreenPreview() {
    val mockParticipants = listOf(
        Participant(id = 1, name = "John Doe", email = "john@example.com"),
        Participant(id = 2, name = "Jane Smith"),
        Participant(id = 3, name = "Bob Wilson")
    )

    val mockResults = listOf(
        BillSplitResult(
            participant = mockParticipants[0],
            assignedItems = listOf(
                BillItem(id = 1, name = "Nasi Goreng", price = 25000.0, quantity = 1)
            ),
            subtotal = 25000.0,
            taxAmount = 2500.0,
            serviceAmount = 1250.0,
            total = 28750.0
        ),
        BillSplitResult(
            participant = mockParticipants[1],
            assignedItems = listOf(
                BillItem(id = 2, name = "Es Teh Manis", price = 8000.0, quantity = 2)
            ),
            subtotal = 16000.0,
            taxAmount = 1600.0,
            serviceAmount = 800.0,
            total = 18400.0
        ),
        BillSplitResult(
            participant = mockParticipants[2],
            assignedItems = listOf(
                BillItem(id = 3, name = "Ayam Bakar", price = 35000.0, quantity = 1)
            ),
            subtotal = 35000.0,
            taxAmount = 3500.0,
            serviceAmount = 1750.0,
            total = 40250.0
        )
    )

    OneVaultTheme {
        SplitResultsScreen()
    }
}
