package com.adilstudio.project.onevault.presentation.bill

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddBillScreen(
    onBillAdded: () -> Unit = {},
    onScanBill: (onResult: (title: String, amount: String, vendor: String) -> Unit) -> Unit = { _ -> }
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var vendor by remember { mutableStateOf("") }
    var billDate by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Add Bill", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = vendor, onValueChange = { vendor = it }, label = { Text("Vendor") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = billDate, onValueChange = { billDate = it }, label = { Text("Bill Date (timestamp)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = imagePath, onValueChange = { imagePath = it }, label = { Text("Image Path") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            onScanBill { scannedTitle, scannedAmount, scannedVendor ->
                title = scannedTitle
                amount = scannedAmount
                vendor = scannedVendor
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Scan Bill")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onBillAdded, modifier = Modifier.fillMaxWidth()) {
            Text("Save Bill")
        }
    }
}
