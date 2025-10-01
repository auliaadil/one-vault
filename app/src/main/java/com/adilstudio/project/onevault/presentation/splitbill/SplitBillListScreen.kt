package com.adilstudio.project.onevault.presentation.splitbill

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.core.util.DateUtil
import com.adilstudio.project.onevault.core.util.RupiahFormatter
import com.adilstudio.project.onevault.domain.model.*
import com.adilstudio.project.onevault.presentation.component.EmptyState
import com.adilstudio.project.onevault.presentation.component.GenericScreen
import com.adilstudio.project.onevault.ui.theme.OneVaultTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitBillListScreen(
    splitBillViewModel: SplitBillViewModel = koinViewModel(),
    onCreateSplitBill: () -> Unit = {},
    onViewSplitBill: (SplitBill) -> Unit = {}
) {
    val splitBills by splitBillViewModel.splitBills.collectAsState()
    val isLoading by splitBillViewModel.isLoading.collectAsState()
    val error by splitBillViewModel.error.collectAsState()
    val successMessage by splitBillViewModel.successMessage.collectAsState()

    LaunchedEffect(Unit) {
        splitBillViewModel.loadSplitBills()
    }

    // Handle messages
    LaunchedEffect(error) {
        error?.let {
            // Show snackbar or toast
            splitBillViewModel.clearMessages()
        }
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            // Show snackbar or toast
            splitBillViewModel.clearMessages()
        }
    }

    GenericScreen(
        title = "Split Bills",
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    splitBillViewModel.startNewSplitBill()
                    onCreateSplitBill()
                }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Create Split Bill"
                )
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            SplitBillListContent(
                splitBills = splitBills,
                onViewSplitBill = onViewSplitBill,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun SplitBillListContent(
    splitBills: List<SplitBill>,
    onViewSplitBill: (SplitBill) -> Unit,
    modifier: Modifier = Modifier
) {
    if (splitBills.isEmpty()) {
        EmptyState(
            title = "No Split Bills",
            description = "Tap the + button to create your first split bill",
            modifier = modifier
        )
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = dimensionResource(R.dimen.spacing_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
        ) {
            items(splitBills) { splitBill ->
                SplitBillCard(
                    splitBill = splitBill,
                    onClick = { onViewSplitBill(splitBill) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitBillCard(
    splitBill: SplitBill,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.spacing_xs))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacing_large))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val formattedDate = DateUtil.isoStringToLocalDate(splitBill.billDate)?.let { date ->
                        DateUtil.formatDateForDisplay(date)
                    } ?: splitBill.billDate

                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = splitBill.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = splitBill.vendor,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = RupiahFormatter.formatWithRupiahPrefix(splitBill.total.toLong()),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs))
                    ) {
                        Icon(
                            Icons.Default.Groups,
                            contentDescription = null,
                            modifier = Modifier.size(dimensionResource(R.dimen.icon_small)),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${splitBill.participants.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

            Text(
                text = "${splitBill.items.size} items • ${splitBill.splitMethod.name.lowercase().replace('_', ' ')} split",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplitBillListScreenPreview() {
    val mockSplitBills = listOf(
        SplitBill(
            id = 1,
            title = "Dinner at Warung Sederhana",
            vendor = "Warung Makan Sederhana",
            billDate = "2024-01-15",
            items = listOf(
                BillItem(id = 1, name = "Nasi Goreng", price = 25000.0, quantity = 2),
                BillItem(id = 2, name = "Es Teh Manis", price = 8000.0, quantity = 3)
            ),
            participants = listOf(
                Participant(id = 1, name = "John Doe"),
                Participant(id = 2, name = "Jane Smith"),
                Participant(id = 3, name = "Bob Wilson")
            ),
            splitMethod = SplitMethod.EQUAL,
            taxPercentage = 10.0,
            servicePercentage = 5.0
        ),
        SplitBill(
            id = 2,
            title = "Lunch Meeting",
            vendor = "Cafe Central",
            billDate = "2024-01-10",
            items = listOf(
                BillItem(id = 1, name = "Coffee", price = 15000.0, quantity = 4),
                BillItem(id = 2, name = "Sandwich", price = 35000.0, quantity = 2)
            ),
            participants = listOf(
                Participant(id = 1, name = "Alice Brown"),
                Participant(id = 2, name = "Charlie Davis")
            ),
            splitMethod = SplitMethod.BY_ITEM
        )
    )

    OneVaultTheme {
        SplitBillListContent(
            splitBills = mockSplitBills,
            onViewSplitBill = {}
        )
    }
}
