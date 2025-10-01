package com.adilstudio.project.onevault.presentation.splitbill

import android.net.Uri
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
import com.adilstudio.project.onevault.domain.model.BillItem
import com.adilstudio.project.onevault.presentation.component.GenericScreen
import com.adilstudio.project.onevault.ui.theme.OneVaultTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun BillCaptureScreen(
    splitBillViewModel: SplitBillViewModel = koinViewModel(),
    onImageCaptured: (Uri) -> Unit = {},
    onProceedToReview: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val isLoading by splitBillViewModel.isLoading.collectAsState()
    val billItems by splitBillViewModel.billItems.collectAsState()
    val error by splitBillViewModel.error.collectAsState()

    var showImagePicker by remember { mutableStateOf(false) }

    GenericScreen(
        title = "Capture Bill",
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(dimensionResource(R.dimen.spacing_large)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                Text("Parsing bill...")
            } else if (billItems.isNotEmpty()) {
                // Show parsed items preview
                Text(
                    text = "Bill parsed successfully!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(dimensionResource(R.dimen.spacing_medium))
                    ) {
                        Text(
                            text = "Detected Items:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

                        billItems.take(3).forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.name} x${item.quantity}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = RupiahFormatter.formatWithRupiahPrefix((item.price * item.quantity).toLong()),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        if (billItems.size > 3) {
                            Text(
                                text = "... and ${billItems.size - 3} more items",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))

                Button(
                    onClick = onProceedToReview,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Review & Edit Items")
                }
            } else {
                // Show capture options
                Icon(
                    Icons.Default.DocumentScanner,
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(R.dimen.icon_extra_large)),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))

                Text(
                    text = "Capture or Upload Bill",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

                Text(
                    text = "Take a photo or select an image to automatically extract bill items",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_extra_large)))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
                ) {
                    Button(
                        onClick = { showImagePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
                        Text("Take Photo")
                    }

                    OutlinedButton(
                        onClick = { showImagePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
                        Text("Choose from Gallery")
                    }

                    TextButton(
                        onClick = {
                            // Skip OCR and go to manual entry
                            splitBillViewModel.startNewSplitBill()
                            onProceedToReview()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Enter Items Manually")
                    }
                }
            }

            error?.let {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(dimensionResource(R.dimen.spacing_medium)),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }

    // Mock image picker - in real implementation, use ActivityResultLauncher
    if (showImagePicker) {
        LaunchedEffect(Unit) {
            showImagePicker = false
            // Mock URI for demonstration
            val mockUri = Uri.parse("content://mock/image")
            splitBillViewModel.parseBillFromImage(mockUri)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BillCaptureScreenPreview() {
    OneVaultTheme {
        BillCaptureScreen()
    }
}
