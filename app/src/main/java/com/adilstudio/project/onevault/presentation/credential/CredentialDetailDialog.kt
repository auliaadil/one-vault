package com.adilstudio.project.onevault.presentation.credential

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.domain.model.Credential
import com.adilstudio.project.onevault.data.security.SecurityManager
import org.koin.compose.koinInject

@Composable
fun CredentialDetailDialog(
    credential: Credential,
    onDismiss: () -> Unit,
    onEdit: (Credential) -> Unit,
    onDelete: (Long) -> Unit
) {
    val context = LocalContext.current
    val securityManager: SecurityManager = koinInject()
    var isPasswordVisible by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Decrypt the password for display
    val decryptedPassword = try {
        decryptPassword(credential.encryptedPassword, securityManager)
    } catch (e: Exception) {
        null
    }
    val usernameLabel = stringResource(R.string.username)
    val passwordLabel = stringResource(R.string.password)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = credential.serviceName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Username Section
                Column {
                    Text(
                        text = stringResource(R.string.username),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = credential.username,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                copyToClipboard(context, usernameLabel, credential.username)
                            }
                        ) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = stringResource(R.string.copy_username),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Password Section
                Column {
                    Text(
                        text = stringResource(R.string.password),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when {
                                decryptedPassword == null -> {
                                    stringResource(R.string.failed_decrypt_password)
                                }
                                isPasswordVisible -> {
                                    decryptedPassword
                                }
                                else -> {
                                    "•".repeat(decryptedPassword.length)
                                }
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Row {
                            IconButton(
                                onClick = { isPasswordVisible = !isPasswordVisible }
                            ) {
                                Icon(
                                    if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (isPasswordVisible) stringResource(R.string.hide_password) else stringResource(R.string.show_password),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = {
                                    copyToClipboard(context, passwordLabel, decryptedPassword.orEmpty())
                                }
                            ) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = stringResource(R.string.copy_password),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Edit Button
                TextButton(
                    onClick = { onEdit(credential) }
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.edit))
                }

                // Delete Button
                TextButton(
                    onClick = { showDeleteConfirmation = true },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.delete))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )

    // Delete Confirmation Dialog
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
                        onDelete(credential.id)
                        showDeleteConfirmation = false
                        onDismiss()
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

private fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(label, text)
    clipboardManager.setPrimaryClip(clipData)
}

private fun decryptPassword(encryptedPassword: String, securityManager: SecurityManager): String {
    return try {
        // This assumes the encrypted password is stored in a specific format
        // You may need to adjust this based on your actual storage format

        // If the password is stored as Base64 encoded combined IV + ciphertext
        val decodedBytes = android.util.Base64.decode(encryptedPassword, android.util.Base64.DEFAULT)

        // Extract IV (first 12 bytes) and ciphertext (remaining bytes)
        val iv = decodedBytes.sliceArray(0..11)
        val cipherText = decodedBytes.sliceArray(12 until decodedBytes.size)

        securityManager.decrypt(iv, cipherText)
    } catch (e: Exception) {
        // If decryption fails, return the original encrypted password
        // This might indicate the password isn't actually encrypted or uses a different format
        encryptedPassword
    }
}
