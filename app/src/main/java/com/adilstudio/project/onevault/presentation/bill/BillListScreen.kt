package com.adilstudio.project.onevault.presentation.bill

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.core.util.DateUtil
import com.adilstudio.project.onevault.core.util.RupiahFormatter
import com.adilstudio.project.onevault.domain.model.Bill
import com.adilstudio.project.onevault.presentation.bill.category.BillCategoryViewModel
import com.adilstudio.project.onevault.presentation.bill.category.createDefaultCategories
import org.koin.androidx.compose.koinViewModel

@Composable
fun BillListScreen(
    billTrackerViewModel: BillTrackerViewModel = koinViewModel(),
    categoryViewModel: BillCategoryViewModel = koinViewModel(),
    onAddBill: () -> Unit = {},
    onManageCategories: () -> Unit = {},
    onEditBill: (Bill) -> Unit = {}
) {
    val bills = billTrackerViewModel.bills.collectAsState().value

    // Initialize default categories if needed
    val defaultCategories = createDefaultCategories()
    LaunchedEffect(Unit) {
        categoryViewModel.checkAndInitializeDefaultCategories(defaultCategories)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header with Categories button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.bills), style = MaterialTheme.typography.headlineMedium)
            OutlinedButton(
                onClick = onManageCategories,
                modifier = Modifier.height(40.dp)
            ) {
                Icon(
                    Icons.Default.Category,
                    contentDescription = stringResource(R.string.manage_categories),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.categories))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(bills.size) { idx ->
                val bill = bills[idx]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = { onEditBill(bill) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(bill.title, style = MaterialTheme.typography.titleMedium)
                                Text("${RupiahFormatter.formatWithRupiahPrefix(bill.amount.toLong())} - ${bill.vendor}")

                                // Display formatted date
                                val formattedDate = DateUtil.isoStringToLocalDate(bill.billDate)?.let { date ->
                                    DateUtil.formatDateForDisplay(date)
                                } ?: bill.billDate
                                Text(
                                    stringResource(R.string.date_label, formattedDate),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                // Display category or "No Category" if null
                                Text(
                                    stringResource(R.string.category_label, bill.category ?: stringResource(R.string.no_category)),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Show attachment icon if image exists
                            if (!bill.imagePath.isNullOrEmpty()) {
                                Icon(
                                    Icons.Default.AttachFile,
                                    contentDescription = stringResource(R.string.has_attachment),
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
        Button(onClick = onAddBill, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.add_bill))
        }
    }
}
