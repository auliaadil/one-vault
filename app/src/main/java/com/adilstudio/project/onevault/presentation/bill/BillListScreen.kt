package com.adilstudio.project.onevault.presentation.bill

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun BillListScreen(
    viewModel: BillTrackerViewModel = koinViewModel(),
    onAddBill: () -> Unit = {}
) {
    val bills = viewModel.bills.collectAsState().value
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Bills", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(bills.size) { idx ->
                val bill = bills[idx]
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(bill.title, style = MaterialTheme.typography.titleMedium)
                        Text("${bill.amount} - ${bill.vendor}")
                    }
                }
            }
        }
        Button(onClick = onAddBill, modifier = Modifier.fillMaxWidth()) {
            Text("Add Bill")
        }
    }
}
