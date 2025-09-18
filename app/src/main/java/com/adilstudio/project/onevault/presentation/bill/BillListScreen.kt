package com.adilstudio.project.onevault.presentation.bill

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.core.util.DateUtil
import com.adilstudio.project.onevault.core.util.RupiahFormatter
import com.adilstudio.project.onevault.domain.model.Bill
import com.adilstudio.project.onevault.presentation.bill.category.BillCategoryViewModel
import com.adilstudio.project.onevault.presentation.bill.category.createDefaultCategories
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
) {
    val bills = billTrackerViewModel.bills.collectAsState().value

    // Initialize default categories if needed
    val defaultCategories = createDefaultCategories()
    LaunchedEffect(Unit) {
        categoryViewModel.checkAndInitializeDefaultCategories(defaultCategories)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.bills)) },
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
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddBill) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_bill)
                )
            }
        }
    ) { paddingValues ->
        BillListContent(
            bills = bills,
            onEditBill = onEditBill,
            modifier = Modifier.padding(paddingValues)
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
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.no_bills_saved),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
                    Text(
                        text = stringResource(R.string.tap_plus_to_add_first_bill),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(modifier = modifier) {
                items(bills.size) { idx ->
                    val bill = bills[idx]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = dimensionResource(R.dimen.spacing_xs)),
                        onClick = { onEditBill(bill) }
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
