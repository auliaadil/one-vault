package com.adilstudio.project.onevault.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportExportScreen(
    onNavigateBack: () -> Unit
) {
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.import_export))
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Warning Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.import_export_warning),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Export Section
            Text(
                text = stringResource(R.string.export_data),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            ImportExportItem(
                icon = Icons.Default.Upload,
                title = stringResource(R.string.export_credentials),
                description = stringResource(R.string.export_credentials_description),
                onClick = { showExportDialog = true }
            )

            ImportExportItem(
                icon = Icons.Default.Receipt,
                title = stringResource(R.string.export_bills),
                description = stringResource(R.string.export_bills_description),
                onClick = { /* TODO: Implement bill export */ }
            )

            ImportExportItem(
                icon = Icons.Default.Folder,
                title = stringResource(R.string.export_vault_files),
                description = stringResource(R.string.export_vault_files_description),
                onClick = { /* TODO: Implement vault files export */ }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Import Section
            Text(
                text = stringResource(R.string.import_data),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            ImportExportItem(
                icon = Icons.Default.Download,
                title = stringResource(R.string.import_credentials),
                description = stringResource(R.string.import_credentials_description),
                onClick = { showImportDialog = true }
            )

            ImportExportItem(
                icon = Icons.Default.Receipt,
                title = stringResource(R.string.import_bills),
                description = stringResource(R.string.import_bills_description),
                onClick = { /* TODO: Implement bill import */ }
            )

            ImportExportItem(
                icon = Icons.Default.Folder,
                title = stringResource(R.string.import_vault_files),
                description = stringResource(R.string.import_vault_files_description),
                onClick = { /* TODO: Implement vault files import */ }
            )
        }
    }

    // Export Confirmation Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Text(text = stringResource(R.string.export_confirmation_title))
            },
            text = {
                Text(text = stringResource(R.string.export_confirmation_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExportDialog = false
                        // TODO: Implement actual export logic
                    }
                ) {
                    Text(stringResource(R.string.export))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExportDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    // Import Confirmation Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = {
                Text(text = stringResource(R.string.import_confirmation_title))
            },
            text = {
                Text(text = stringResource(R.string.import_confirmation_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImportDialog = false
                        // TODO: Implement actual import logic
                    }
                ) {
                    Text(stringResource(R.string.import_text))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showImportDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun ImportExportItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
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
