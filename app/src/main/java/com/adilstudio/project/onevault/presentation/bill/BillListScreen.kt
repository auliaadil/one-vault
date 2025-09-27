package com.adilstudio.project.onevault.presentation.bill

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.core.util.DateUtil
import com.adilstudio.project.onevault.core.util.RupiahFormatter
import com.adilstudio.project.onevault.domain.model.Bill
import com.adilstudio.project.onevault.presentation.bill.category.BillCategoryViewModel
import com.adilstudio.project.onevault.presentation.bill.category.createDefaultCategories
import com.adilstudio.project.onevault.presentation.component.EmptyState
import com.adilstudio.project.onevault.presentation.component.GenericScreen
import com.adilstudio.project.onevault.ui.theme.OneVaultTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillListScreen(
    billTrackerViewModel: BillTrackerViewModel = koinViewModel(),
    categoryViewModel: BillCategoryViewModel = koinViewModel(),
    onAddBill: () -> Unit = {},
    onManageCategories: () -> Unit = {},
    onEditBill: (Bill) -> Unit = {},
    onManageAccounts: () -> Unit = {},
    onAddBillWithScannedImage: (Uri) -> Unit = {} // Changed to pass image URI
) {
    val bills = billTrackerViewModel.bills.collectAsState().value

    // Scanner dialog state
    var showScannerDialog by remember { mutableStateOf(false) }

    // Initialize default categories if needed
    val defaultCategories = createDefaultCategories()
    LaunchedEffect(Unit) {
        categoryViewModel.checkAndInitializeDefaultCategories(defaultCategories)
    }

    LaunchedEffect(Unit) {
        billTrackerViewModel.loadBills()
    }

    GenericScreen(
        title = stringResource(R.string.bills),
        actions = {
            OutlinedButton(onClick = { showScannerDialog = true }) {
                Icon(
                    Icons.Default.DocumentScanner,
                    contentDescription = stringResource(R.string.scan_bill)
                )
            }
            OutlinedButton(onClick = onManageCategories) {
                Icon(
                    Icons.Default.Category,
                    contentDescription = stringResource(R.string.manage_categories)
                )
            }
            OutlinedButton(onClick = onManageAccounts) {
                Icon(
                    Icons.Default.AccountBalanceWallet,
                    contentDescription = stringResource(R.string.accounts)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddBill) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_bill)
                )
            }
        },
        defaultPaddingHorizontal = R.dimen.spacing_none
    ) { paddingValues ->
        BillListContent(
            bills = bills,
            onEditBill = onEditBill,
            modifier = Modifier.padding(paddingValues)
        )
    }

    // Scanner Dialog
    if (showScannerDialog) {
        BillScannerDialog(
            onDismiss = { showScannerDialog = false },
            onImageCaptured = { imageUri ->
                showScannerDialog = false
                onAddBillWithScannedImage(imageUri)
            }
        )
    }
}

@Composable
fun BillListContent(
    bills: List<Bill>,
    onEditBill: (Bill) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        if (bills.isEmpty()) {
            EmptyState(
                title = stringResource(R.string.no_bills_saved),
                description = stringResource(R.string.tap_plus_to_add_first_bill),
                modifier = modifier
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.spacing_large)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
            ) {
                items(bills.size) { idx ->
                    val bill = bills[idx]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        onClick = { onEditBill(bill) },
                        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.spacing_xs))
                    ) {
                        Column(modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large))) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(bill.title, style = MaterialTheme.typography.titleMedium)
                                    Text("${RupiahFormatter.formatWithRupiahPrefix(bill.amount.toLong())} - ${bill.vendor}")

                                    // Display formatted date
                                    val formattedDate =
                                        DateUtil.isoStringToLocalDate(bill.billDate)?.let { date ->
                                            DateUtil.formatDateForDisplay(date)
                                        } ?: bill.billDate
                                    Text(
                                        stringResource(R.string.date_label, formattedDate),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    // Display category or "No Category" if null
                                    Text(
                                        stringResource(
                                            R.string.category_label,
                                            bill.category ?: stringResource(R.string.no_category)
                                        ),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Show attachment icon if image exists
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (bill.imagePath != null) {
                                        Icon(
                                            imageVector = Icons.Default.AttachFile,
                                            contentDescription = stringResource(R.string.has_attachment),
                                            modifier = Modifier.size(dimensionResource(R.dimen.icon_size_small)),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BillListScreenPreview() {
    // Create mock bills for preview
    val mockBills = listOf(
        Bill(
            id = 1,
            title = "Electric Bill",
            vendor = "PLN",
            amount = 500000.0,
            billDate = "2024-01-15",
            category = "Utilities",
            imagePath = null
        ),
        Bill(
            id = 2,
            title = "Internet Bill",
            vendor = "Telkom",
            amount = 300000.0,
            billDate = "2024-01-10",
            category = "Utilities",
            imagePath = "/path/to/image"
        )
    )

    OneVaultTheme(
        darkTheme = true
    ) {
        GenericScreen(
            title = stringResource(R.string.bills),
            actions = {
                OutlinedButton(onClick = { }) {
                    Icon(
                        Icons.Default.DocumentScanner,
                        contentDescription = stringResource(R.string.scan_bill)
                    )
                }
                OutlinedButton(onClick = { }) {
                    Icon(
                        Icons.Default.Category,
                        contentDescription = stringResource(R.string.manage_categories)
                    )
                }
                OutlinedButton(onClick = {}) {
                    Icon(
                        Icons.Default.AccountBalanceWallet,
                        contentDescription = stringResource(R.string.accounts)
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {}) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_bill)
                    )
                }
            },
            defaultPaddingHorizontal = R.dimen.spacing_none
        ) {
            BillListContent(
                bills = mockBills,
                onEditBill = {}
            )
        }
    }
}
