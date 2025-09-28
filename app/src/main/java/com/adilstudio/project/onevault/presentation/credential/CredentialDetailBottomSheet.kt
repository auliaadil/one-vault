package com.adilstudio.project.onevault.presentation.credential

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.domain.model.Credential
import com.adilstudio.project.onevault.presentation.component.DetailField
import com.adilstudio.project.onevault.presentation.component.GenericBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CredentialDetailBottomSheet(
    credential: Credential,
    onDismiss: () -> Unit,
    onEdit: (Credential) -> Unit,
    onDelete: (Long) -> Unit
) {
    val clipboard = LocalClipboard.current
    var showPassword by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val copyServiceNameMessage =
        stringResource(R.string.has_been_copied, stringResource(R.string.service_name))
    val copyUsernameMessage =
        stringResource(R.string.has_been_copied, stringResource(R.string.username_email))
    val copyPasswordMessage =
        stringResource(R.string.has_been_copied, stringResource(R.string.password))

    GenericBottomSheet(
        title = stringResource(R.string.credential_details),
        onEdit = { onEdit(credential) },
        onDelete = {}, // handled by deleteDialogText and onDeleteConfirmed
        deleteDialogText = stringResource(R.string.delete_credential_message, credential.serviceName),
        onDeleteConfirmed = {
            onDelete(credential.id)
            onDismiss()
        },
        onDismiss = onDismiss
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_large))
        ) {
            DetailField(
                labelRes = R.string.service_name,
                value = credential.serviceName,
                fontWeight = FontWeight.Medium,
                trailing = {
                    IconButton(
                        onClick = {
                            val clipData = ClipData.newPlainText("Service Name", credential.serviceName)
                            clipboard.nativeClipboard.setPrimaryClip(clipData)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message = copyServiceNameMessage)
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = stringResource(R.string.copy_service_name),
                            modifier = Modifier.size(dimensionResource(R.dimen.spacing_xl))
                        )
                    }
                }
            )
            DetailField(
                labelRes = R.string.username_email,
                value = credential.username,
                trailing = {
                    IconButton(
                        onClick = {
                            val clipData = ClipData.newPlainText("Username", credential.username)
                            clipboard.nativeClipboard.setPrimaryClip(clipData)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message = copyUsernameMessage)
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = stringResource(R.string.copy_username),
                            modifier = Modifier.size(dimensionResource(R.dimen.spacing_xl))
                        )
                    }
                }
            )
            DetailField(
                labelRes = R.string.password,
                value = if (showPassword) credential.password else "*".repeat(credential.password.length),
                maxLines = if (showPassword) Int.MAX_VALUE else 1,
                trailing = {
                    Row {
                        IconButton(
                            onClick = { showPassword = !showPassword }
                        ) {
                            Icon(
                                if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (showPassword) stringResource(R.string.hide_password) else stringResource(R.string.show_password),
                                modifier = Modifier.size(dimensionResource(R.dimen.spacing_xl))
                            )
                        }
                        IconButton(
                            onClick = {
                                val clipData = ClipData.newPlainText("Password", credential.password)
                                clipboard.nativeClipboard.setPrimaryClip(clipData)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(message = copyPasswordMessage)
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = stringResource(R.string.copy_password),
                                modifier = Modifier.size(dimensionResource(R.dimen.spacing_xl))
                            )
                        }
                    }
                }
            )
            // Password Template Info
            if (!credential.passwordTemplate.isNullOrBlank()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.template_based_password),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))
                        Text(
                            text = stringResource(R.string.template_password_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.close))
                }
                Button(
                    onClick = { onEdit(credential) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.edit))
                }
            }
            // Snackbar
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomCenter
            ) {
                SnackbarHost(hostState = snackbarHostState)
            }
        }
    }
}
