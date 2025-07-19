package com.adilstudio.project.onevault.core.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Utility class for handling permissions in the OneVault app
 */
object PermissionUtil {

    /**
     * Check if a specific permission is granted
     */
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if camera permission is granted
     */
    fun isCameraPermissionGranted(context: Context): Boolean {
        return isPermissionGranted(context, Manifest.permission.CAMERA)
    }

    /**
     * Get required permissions for camera functionality
     */
    fun getCameraPermissions(): Array<String> {
        return arrayOf(Manifest.permission.CAMERA)
    }

    /**
     * Check if all required permissions are granted
     */
    fun areAllPermissionsGranted(context: Context, permissions: Array<String>): Boolean {
        return permissions.all { isPermissionGranted(context, it) }
    }
}

/**
 * Permission state for Compose
 */
data class PermissionState(
    val isGranted: Boolean = false,
    val shouldShowRationale: Boolean = false,
    val isPermanentlyDenied: Boolean = false
)

/**
 * Composable function to handle camera permission
 */
@Composable
fun rememberCameraPermissionState(
    onPermissionResult: (PermissionState) -> Unit = {}
): CameraPermissionState {
    val context = LocalContext.current
    var permissionState by remember {
        mutableStateOf(
            PermissionState(isGranted = PermissionUtil.isCameraPermissionGranted(context))
        )
    }

    return remember {
        CameraPermissionState(
            permissionState = permissionState,
            onPermissionStateChange = { newState ->
                permissionState = newState
                onPermissionResult(newState)
            }
        )
    }
}

/**
 * Camera permission state wrapper
 */
class CameraPermissionState(
    val permissionState: PermissionState,
    private val onPermissionStateChange: (PermissionState) -> Unit
) {
    fun updatePermissionState(isGranted: Boolean, shouldShowRationale: Boolean = false) {
        val newState = PermissionState(
            isGranted = isGranted,
            shouldShowRationale = shouldShowRationale,
            isPermanentlyDenied = !isGranted && !shouldShowRationale
        )
        onPermissionStateChange(newState)
    }
}
