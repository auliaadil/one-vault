package com.adilstudio.project.onevault.presentation.bill

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import com.adilstudio.project.onevault.domain.model.Bill
import com.adilstudio.project.onevault.core.util.PermissionUtil
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddBillScreen(
    viewModel: BillTrackerViewModel = koinViewModel(),
    onBillAdded: () -> Unit = {}
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var vendor by remember { mutableStateOf("") }
    var billDate by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf("") }

    // MLKit scanning state
    var scannedTexts by remember { mutableStateOf<List<String>>(emptyList()) }
    var showTextSelectionDialog by remember { mutableStateOf(false) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var isScanning by remember { mutableStateOf(false) }

    // Permission state
    var showPermissionDialog by remember { mutableStateOf(false) }
    var hasCameraPermission by remember {
        mutableStateOf(PermissionUtil.isCameraPermissionGranted(context))
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            isScanning = true
            val image = InputImage.fromFilePath(context, cameraImageUri!!)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val texts = visionText.textBlocks
                        .flatMap { it.lines }
                        .map { it.text.trim() }
                        .filter { it.isNotEmpty() }

                    scannedTexts = texts
                    isScanning = false

                    if (texts.isNotEmpty()) {
                        showTextSelectionDialog = true
                    }
                }
                .addOnFailureListener {
                    isScanning = false
                    // Handle error if needed
                }
        }
    }

    // Function to launch camera
    fun launchCamera() {
        val imageFile = File(context.cacheDir, "camera_image_${System.currentTimeMillis()}.jpg")
        cameraImageUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
        cameraImageUri?.let { uri ->
            cameraLauncher.launch(uri)
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            // Permission granted, proceed with camera
            launchCamera()
        } else {
            // Permission denied, show explanation
            showPermissionDialog = true
        }
    }

    // Function to handle scan button click
    fun handleScanButtonClick() {
        if (hasCameraPermission) {
            launchCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Add Bill", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = vendor,
            onValueChange = { vendor = it },
            label = { Text("Vendor") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = billDate,
            onValueChange = { billDate = it },
            label = { Text("Bill Date (timestamp)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = imagePath,
            onValueChange = { imagePath = it },
            label = { Text("Image Path") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Scan Bill Button
        Button(
            onClick = { handleScanButtonClick() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isScanning
        ) {
            if (isScanning) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scanning...")
            } else {
                Text("Scan Bill")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Save Bill Button
        Button(
            onClick = {
                val bill = Bill(
                    id = System.currentTimeMillis(),
                    title = title,
                    category = category,
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    vendor = vendor,
                    billDate = billDate.toLongOrNull() ?: System.currentTimeMillis(),
                    imagePath = imagePath
                )
                viewModel.addBill(bill)
                onBillAdded()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Bill")
        }
    }

    // Permission explanation dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Camera Permission Required") },
            text = {
                Text("This app needs camera permission to scan bills and extract text information. Please grant camera permission to use this feature.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                ) {
                    Text("Grant Permission")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Text Selection Dialog
    if (showTextSelectionDialog) {
        TextSelectionDialog(
            scannedTexts = scannedTexts,
            onTextSelected = { selectedTitle, selectedAmount, selectedVendor ->
                if (selectedTitle.isNotEmpty()) title = selectedTitle
                if (selectedAmount.isNotEmpty()) amount = selectedAmount
                if (selectedVendor.isNotEmpty()) vendor = selectedVendor
                showTextSelectionDialog = false
            },
            onDismiss = { showTextSelectionDialog = false }
        )
    }
}

@Composable
fun TextSelectionDialog(
    scannedTexts: List<String>,
    onTextSelected: (title: String, amount: String, vendor: String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTitle by remember { mutableStateOf("") }
    var selectedAmount by remember { mutableStateOf("") }
    var selectedVendor by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Scanned Text") },
        text = {
            Column {
                Text("Tap on text to assign to fields:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))

                // Show current selections
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Selected:", style = MaterialTheme.typography.labelMedium)
                        Text("Title: ${selectedTitle.ifEmpty { "None" }}", style = MaterialTheme.typography.bodySmall)
                        Text("Amount: ${selectedAmount.ifEmpty { "None" }}", style = MaterialTheme.typography.bodySmall)
                        Text("Vendor: ${selectedVendor.ifEmpty { "None" }}", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.height(200.dp)) {
                    itemsIndexed(scannedTexts) { index, text ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when (text) {
                                    selectedTitle -> MaterialTheme.colorScheme.primaryContainer
                                    selectedAmount -> MaterialTheme.colorScheme.secondaryContainer
                                    selectedVendor -> MaterialTheme.colorScheme.tertiaryContainer
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            )
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(text, style = MaterialTheme.typography.bodyMedium)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Button(
                                        onClick = { selectedTitle = text },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectedTitle == text)
                                                MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.outline
                                        )
                                    ) {
                                        Text("Title", style = MaterialTheme.typography.labelSmall)
                                    }

                                    Button(
                                        onClick = { selectedAmount = text },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectedAmount == text)
                                                MaterialTheme.colorScheme.secondary
                                            else MaterialTheme.colorScheme.outline
                                        )
                                    ) {
                                        Text("Amount", style = MaterialTheme.typography.labelSmall)
                                    }

                                    Button(
                                        onClick = { selectedVendor = text },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectedVendor == text)
                                                MaterialTheme.colorScheme.tertiary
                                            else MaterialTheme.colorScheme.outline
                                        )
                                    ) {
                                        Text("Vendor", style = MaterialTheme.typography.labelSmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onTextSelected(selectedTitle, selectedAmount, selectedVendor)
                }
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
