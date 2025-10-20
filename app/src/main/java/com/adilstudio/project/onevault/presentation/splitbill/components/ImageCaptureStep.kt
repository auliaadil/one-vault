package com.adilstudio.project.onevault.presentation.splitbill.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.domain.util.FeatureFlag
import com.adilstudio.project.onevault.presentation.splitbill.SplitBillViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageCaptureStep(
    viewModel: SplitBillViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && imageUri != null) {
            viewModel.onImageCaptured(imageUri.toString())
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.onImageCaptured(it.toString())
        }
    }

    // Function to launch camera with proper URI creation
    val launchCamera = {
        try {
            val photoFile = File(context.cacheDir, "split_bill_${System.currentTimeMillis()}.jpg")
            val photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            imageUri = photoUri
            cameraLauncher.launch(photoUri)
        } catch (e: Exception) {
            // Handle FileProvider errors gracefully
            e.printStackTrace()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = dimensionResource(R.dimen.spacing_large)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (FeatureFlag.isOcrSplitBillEnabled()) {
            // Show OCR-focused UI when OCR is enabled
            Icon(
                Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_size_xxl)),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xxxl)))

            Text(
                text = stringResource(R.string.capture_receipt_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))

            Text(
                text = stringResource(R.string.capture_receipt_description),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

            Button(
                onClick = launchCamera,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Take Photo")
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))

            OutlinedButton(
                onClick = {
                    galleryLauncher.launch("image/*")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Choose from Gallery")
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xxxl)))

            TextButton(
                onClick = { viewModel.skipImageCapture() }
            ) {
                Text("Skip and Enter Manually")
            }
        } else {
            // Show manual entry focused UI when OCR is disabled
            Icon(
                Icons.Default.Receipt,
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_size_xxl)),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xxxl)))

            Text(
                text = "Create Split Bill",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))

            Text(
                text = "Enter your bill details manually to split costs among participants",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xl)))

            Button(
                onClick = { viewModel.skipImageCapture() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Enter Details")
            }
        }
    }
}
