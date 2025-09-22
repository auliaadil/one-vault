package com.adilstudio.project.onevault.presentation.credential

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.domain.model.Credential
import com.adilstudio.project.onevault.presentation.component.EmptyState
import com.adilstudio.project.onevault.presentation.component.GenericScreen
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CredentialListScreen(
    viewModel: CredentialListViewModel = koinViewModel(),
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
            // Auto-clear success message after showing
            viewModel.clearSuccessMessage()
        }
    }

    // Handle error messages
    LaunchedEffect(error) {
        error?.let {
            // Auto-clear error message after showing
            viewModel.clearError()
        }
    }

    GenericScreen(
        title = stringResource(R.string.credentials),
        successMessage = successMessage,
        errorMessage = error,
        onClearSuccess = { viewModel.clearSuccessMessage() },
        onClearError = { viewModel.clearError() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCredential
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_credential)
                )
            }
        },
        defaultPaddingHorizontal = R.dimen.spacing_none
    ) { paddingValues ->
        if (credentials.isEmpty()) {
            EmptyState(
                title = stringResource(R.string.no_credentials_saved),
                description = stringResource(R.string.tap_plus_to_add_first_credential),
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            // Credentials list
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.spacing_large)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small)),
            ) {
                items(
                    items = credentials,
                    key = { it.id }
                ) { credential ->
                    CredentialCard(
                        credential = credential,
                        onClick = { selectedCredential = credential },
                        onEdit = { onEditCredential(credential) },
                        onDelete = { viewModel.deleteCredential(credential.id) }
                    )
                }
            }
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
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.spacing_xs))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacing_large))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = credential.serviceName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))
                    Text(
                        text = credential.username,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Show if it has a password template
                    if (!credential.passwordTemplate.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.template_based),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(
                                    horizontal = dimensionResource(R.dimen.spacing_small),
                                    vertical = dimensionResource(R.dimen.spacing_xxs)
                                )
                            )
                        }
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = { showDeleteConfirmation = true }
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_credential),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(stringResource(R.string.delete_credential)) },
            text = {
                Text(stringResource(R.string.delete_credential_message, credential.serviceName))
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
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
