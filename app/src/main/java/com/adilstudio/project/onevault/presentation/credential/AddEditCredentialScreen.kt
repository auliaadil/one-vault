package com.adilstudio.project.onevault.presentation.credential

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.domain.model.Credential
import com.adilstudio.project.onevault.data.security.SecurityManager
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCredentialScreen(
    credential: Credential? = null, // null for add, non-null for edit
    onSave: (service: String, username: String, password: String) -> Unit,
    onNavigateBack: () -> Unit,
    onCancel: () -> Unit = {}
) {
    val securityManager: SecurityManager = koinInject()
    val isEditing = credential != null

    // Decrypt password for editing if credential exists
    val decryptedPassword = remember(credential) {
        credential?.let { cred ->
            try {
                decryptPassword(cred.encryptedPassword, securityManager)
            } catch (e: Exception) {
                "" // Empty if decryption fails
            }
        } ?: ""
    }

    var serviceName by remember { mutableStateOf(credential?.serviceName ?: "") }
    var username by remember { mutableStateOf(credential?.username ?: "") }
    var password by remember { mutableStateOf(decryptedPassword) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) stringResource(R.string.edit_credential) else stringResource(
                            R.string.add_credential
                        )
                    )
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
        ) {
            OutlinedTextField(
                value = serviceName,
                onValueChange = { serviceName = it },
                label = { Text(stringResource(R.string.service_name)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(stringResource(R.string.username) + " ${stringResource(R.string.required_field)}") },
                modifier = Modifier.fillMaxWidth(),
                isError = username.isBlank(),
                supportingText = if (username.isBlank()) {
                    {
                        Text(
                            stringResource(R.string.username_required),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else null
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password) + " ${stringResource(R.string.required_field)}") },
                modifier = Modifier.fillMaxWidth(),
                isError = password.isBlank(),
                supportingText = if (password.isBlank()) {
                    {
                        Text(
                            stringResource(R.string.password_required),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else null
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isEditing) Arrangement.spacedBy(8.dp) else Arrangement.Center
            ) {
                if (isEditing) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }

                Button(
                    onClick = {
                        if (serviceName.isNotBlank() && username.isNotBlank() && password.isNotBlank()) {
                            // Encrypt password before saving
                            val encryptedPassword = try {
                                encryptPassword(password, securityManager)
                            } catch (e: Exception) {
                                password // Fallback to plain text if encryption fails
                            }
                            onSave(serviceName, username, encryptedPassword)
                        }
                    },
                    modifier = if (isEditing) Modifier.weight(1f) else Modifier.fillMaxWidth(),
                    enabled = serviceName.isNotBlank() && username.isNotBlank() && password.isNotBlank()
                ) {
                    Text(if (isEditing) stringResource(R.string.save_changes) else stringResource(R.string.save_credential))
                }
            }
        }
    }
}

private fun decryptPassword(encryptedPassword: String, securityManager: SecurityManager): String {
    return try {
        val decodedBytes =
            android.util.Base64.decode(encryptedPassword, android.util.Base64.DEFAULT)
        val iv = decodedBytes.sliceArray(0..11)
        val cipherText = decodedBytes.sliceArray(12 until decodedBytes.size)
        securityManager.decrypt(iv, cipherText)
    } catch (e: Exception) {
        encryptedPassword // Return original if decryption fails
    }
}

private fun encryptPassword(plainPassword: String, securityManager: SecurityManager): String {
    return try {
        val (iv, encryptedData) = securityManager.encrypt(plainPassword)
        val combined = iv + encryptedData
        android.util.Base64.encodeToString(combined, android.util.Base64.DEFAULT)
    } catch (e: Exception) {
        plainPassword // Return plain text if encryption fails
    }
}
