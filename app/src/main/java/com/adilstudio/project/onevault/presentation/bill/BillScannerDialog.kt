package com.adilstudio.project.onevault.presentation.bill

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.core.util.PermissionUtil
import java.io.File

@Composable
fun BillScannerDialog(
    onDismiss: () -> Unit,
    onImageCaptured: (Uri) -> Unit
) {
    val context = LocalContext.current

    // State for image capture
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    // Permission state
    var showPermissionDialog by remember { mutableStateOf(false) }
    var hasCameraPermission by remember {
        mutableStateOf(PermissionUtil.isCameraPermissionGranted(context))
    }

    // Image picker launcher for gallery
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onImageCaptured(it)
        } ?: onDismiss()
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraImageUri != null) {
            onImageCaptured(cameraImageUri!!)
        } else {
            onDismiss()
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
            launchCamera()
        } else {
            showPermissionDialog = true
        }
    }

    // Function to handle camera button click
    fun handleCameraClick() {
        if (hasCameraPermission) {
            launchCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Function to handle gallery button click
    fun handleGalleryClick() {
        imagePickerLauncher.launch("image/*")
    }

    // Scan method selection dialog
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.scan_bill)) },
        text = {
            Column {
                Text(
                    stringResource(R.string.choose_scan_method),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.scanner_dialog_spacing)))

                // Camera option
                OutlinedButton(
                    onClick = { handleCameraClick() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.scanner_dialog_icon_size))
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.scanner_dialog_icon_spacing)))
                    Text(stringResource(R.string.scan_with_camera))
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.scanner_dialog_icon_spacing)))

                // Gallery option
                OutlinedButton(
                    onClick = { handleGalleryClick() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.scanner_dialog_icon_size))
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.scanner_dialog_icon_spacing)))
                    Text(stringResource(R.string.scan_from_gallery))
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )

    // Permission explanation dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text(stringResource(R.string.camera_permission_required)) },
            text = {
                Text(stringResource(R.string.camera_permission_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                ) {
                    Text(stringResource(R.string.grant_permission))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        onDismiss()
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
