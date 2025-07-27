package com.adilstudio.project.onevault.presentation.bill

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.core.util.RupiahFormatter
import org.koin.androidx.compose.koinViewModel

@Composable
fun BillListScreen(
    viewModel: BillTrackerViewModel = koinViewModel(),
    onAddBill: () -> Unit = {},
    onManageCategories: () -> Unit = {}
) {
    val bills = viewModel.bills.collectAsState().value
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header with Categories button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Bills", style = MaterialTheme.typography.headlineMedium)
            OutlinedButton(
                onClick = onManageCategories,
                modifier = Modifier.height(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_category_24dp),
                    contentDescription = "Manage Categories",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Categories")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(bills.size) { idx ->
                val bill = bills[idx]
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(bill.title, style = MaterialTheme.typography.titleMedium)
                        Text("${RupiahFormatter.formatWithRupiahPrefix(bill.amount.toLong())} - ${bill.vendor}")
                        if (bill.category.isNotEmpty()) {
                            Text(
                                "Category: ${bill.category}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        Button(onClick = onAddBill, modifier = Modifier.fillMaxWidth()) {
            Text("Add Bill")
        }
    }
}
