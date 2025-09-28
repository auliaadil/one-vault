package com.adilstudio.project.onevault.presentation.bill

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
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
    onAddBillWithScannedImage: (Uri) -> Unit = {}, // Changed to pass image URI
    showScannerDialog: Boolean = false // New parameter for tile service
) {
    val bills = billTrackerViewModel.bills.collectAsState().value

    // Scanner dialog state
    var showScannerDialogState by remember { mutableStateOf(showScannerDialog) }

    // Bottom sheet state
    var selectedBill by remember { mutableStateOf<Bill?>(null) }
    var showBillDetailSheet by remember { mutableStateOf(false) }

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
            Column {
                FloatingActionButton(onClick = { showScannerDialogState = true }) {
                    Icon(
                        Icons.Default.DocumentScanner,
                        contentDescription = stringResource(R.string.scan_bill)
                    )
                }
                Spacer(modifier = Modifier.size(dimensionResource(R.dimen.spacing_small)))
                FloatingActionButton(onClick = onAddBill) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_bill)
                    )
                }
            }
        },
        defaultPaddingHorizontal = R.dimen.spacing_none
    ) { paddingValues ->
        BillListContent(
            bills = bills,
            onViewDetail = { bill ->
                selectedBill = bill
                showBillDetailSheet = true
            },
            modifier = Modifier.padding(paddingValues)
        )
    }

    // Bill Detail Bottom Sheet
    if (showBillDetailSheet && selectedBill != null) {
        BillDetailBottomSheet(
            bill = selectedBill!!,
            onDismiss = {
                showBillDetailSheet = false
                selectedBill = null
            },
            onEdit = { bill ->
                showBillDetailSheet = false
                selectedBill = null
                onEditBill(bill)
            },
            onDelete = { billId ->
                showBillDetailSheet = false
                selectedBill = null
                billTrackerViewModel.deleteBill(billId)
            }
        )
    }

    // Scanner Dialog
    if (showScannerDialogState) {
        BillScannerDialog(
            onDismiss = { showScannerDialogState = false },
            onImageCaptured = { imageUri ->
                showScannerDialogState = false
                onAddBillWithScannedImage(imageUri)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillCard(
    bill: Bill,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
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
                    Text(RupiahFormatter.formatWithRupiahPrefix(bill.amount.toLong()))
                    Text(
                        stringResource(R.string.vendor_field, bill.vendor),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val formattedDate = DateUtil.isoStringToLocalDate(bill.billDate)?.let { date ->
                        DateUtil.formatDateForDisplay(date)
                    } ?: bill.billDate
                    Text(
                        stringResource(R.string.date_label, formattedDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        stringResource(
                            R.string.category_label,
                            bill.category ?: stringResource(R.string.no_category)
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun BillListContent(
    bills: List<Bill>,
    onViewDetail: (Bill) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
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
                    BillCard(
                        bill = bill,
                        onClick = { onViewDetail(bill) }
                    )
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
                onViewDetail = {}
            )
        }
    }
}
