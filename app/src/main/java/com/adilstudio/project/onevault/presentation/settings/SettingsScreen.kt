package com.adilstudio.project.onevault.presentation.settings

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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.adilstudio.project.onevault.BuildConfig
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.presentation.component.GenericScreen
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToAbout: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToImportExport: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val biometricEnabled by viewModel.biometricEnabled.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    GenericScreen(
        title = stringResource(R.string.settings),
        errorMessage = errorMessage,
        onClearError = { viewModel.clearErrorMessage() },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // About Us
            SettingsItem(
                icon = Icons.Default.Info,
                title = stringResource(R.string.about_us),
                onClick = onNavigateToAbout
            )

            // Privacy Policy
            SettingsItem(
                icon = Icons.Default.PrivacyTip,
                title = stringResource(R.string.privacy_policy),
                onClick = onNavigateToPrivacyPolicy
            )

            /* TODO: Implement Import/Export feature
            SettingsItem(
                icon = Icons.Default.ImportExport,
                title = stringResource(R.string.import_export),
                onClick = onNavigateToImportExport
            )
             */

            // Biometric Toggle
            SettingsToggleItem(
                icon = Icons.Default.Fingerprint,
                title = stringResource(R.string.biometric_authentication),
                checked = biometricEnabled,
                onCheckedChange = { enabled ->
                    if (enabled) {
                        activity?.let { fragmentActivity ->
                            viewModel.enableBiometric(fragmentActivity)
                        } ?: run {
                            // Show error if we can't get FragmentActivity
                            viewModel.setErrorMessage(context.getString(R.string.biometric_error_generic))
                        }
                    } else {
                        viewModel.disableBiometric()
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            // App Version
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${stringResource(R.string.app_version)}: ${
                        stringResource(
                            R.string.version_format,
                            BuildConfig.VERSION_NAME,
                            BuildConfig.VERSION_CODE
                        )
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacing_large)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_large)))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsClickableItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacing_large)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_large)))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacing_large)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_large)))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
