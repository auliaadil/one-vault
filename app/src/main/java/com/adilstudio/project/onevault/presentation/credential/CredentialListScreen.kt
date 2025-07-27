package com.adilstudio.project.onevault.presentation.credential

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.domain.model.Credential
import org.koin.androidx.compose.koinViewModel

@Composable
fun CredentialListScreen(
    viewModel: PasswordManagerViewModel = koinViewModel(),
    onAddCredential: () -> Unit,
    onEditCredential: (Credential) -> Unit = {}
) {
    val credentials by viewModel.credentials.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedCredential by remember { mutableStateOf<Credential?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadCredentials()
    }

    // Handle success messages
    LaunchedEffect(successMessage) {
        successMessage?.let {
            viewModel.clearSuccessMessage()
        }
    }

    // Handle error messages
    LaunchedEffect(error) {
        error?.let {
            viewModel.clearError()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Credentials", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Success/Error message display
        successMessage?.let { message ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        error?.let { message ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(credentials.size) { idx ->
                val credential = credentials[idx]
                CredentialCard(
                    credential = credential,
                    onClick = { selectedCredential = credential },
                    onDelete = { viewModel.deleteCredential(credential.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Button(onClick = onAddCredential, modifier = Modifier.fillMaxWidth()) {
            Text("Add Credential")
        }
    }

    // Show credential detail dialog
    selectedCredential?.let { credential ->
        CredentialDetailDialog(
            credential = credential,
            onDismiss = { selectedCredential = null },
            onEdit = {
                selectedCredential = null
                onEditCredential(it)
            },
            onDelete = { id ->
                viewModel.deleteCredential(id)
                selectedCredential = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CredentialCard(
    credential: Credential,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = credential.serviceName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = credential.username,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { showDeleteConfirmation = true }
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Credential",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Credential") },
            text = {
                Text("Are you sure you want to delete the credential for '${credential.serviceName}'? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
