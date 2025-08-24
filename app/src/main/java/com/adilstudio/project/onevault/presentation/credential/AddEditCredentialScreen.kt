package com.adilstudio.project.onevault.presentation.credential

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.data.security.SecurityManager
import com.adilstudio.project.onevault.domain.model.Credential
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCredentialScreen(
    credential: Credential? = null,
    onSaveSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: AddEditCredentialViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val securityManager: SecurityManager = koinInject()
    val clipboardManager = LocalClipboardManager.current

    var passwordVisible by remember { mutableStateOf(false) }

    // Initialize for editing if credential is provided
    LaunchedEffect(credential) {
        credential?.let { cred ->
            viewModel.initializeForEdit(cred)
            viewModel.onPasswordChanged(cred.encryptedPassword)
        }
    }

    // Handle save success
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onSaveSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isEditing)
                            stringResource(R.string.edit_credential)
                        else
                            stringResource(R.string.add_credential)
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
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.spacing_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
        ) {
            // Service Name Field
            OutlinedTextField(
                value = uiState.serviceName,
                onValueChange = viewModel::onServiceNameChanged,
                label = { Text(stringResource(R.string.service_name) + " ${stringResource(R.string.required_field)}") },
                placeholder = { Text(stringResource(R.string.service_name_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                isError = !uiState.isServiceNameValid,
                supportingText = if (!uiState.isServiceNameValid) {
                    { Text(stringResource(R.string.service_name_required)) }
                } else null
            )

            // Username Field
            OutlinedTextField(
                value = uiState.username,
                onValueChange = viewModel::onUsernameChanged,
                label = { Text(stringResource(R.string.username) + " ${stringResource(R.string.required_field)}") },
                placeholder = { Text(stringResource(R.string.username_email_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                isError = !uiState.isUsernameValid,
                supportingText = if (!uiState.isUsernameValid) {
                    { Text(stringResource(R.string.username_required)) }
                } else null
            )

            // Password Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_medium))
            ) {
                Column(
                    modifier = Modifier.padding(dimensionResource(R.dimen.spacing_medium)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.password_section),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // AI Generator Toggle
                            FilterChip(
                                selected = uiState.showPasswordGenerator,
                                onClick = { viewModel.onShowPasswordGeneratorChanged(!uiState.showPasswordGenerator) },
                                label = { Text(stringResource(R.string.ai_generator)) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Psychology,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }
                    }

                    // Password Input Field
                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChanged,
                        label = { Text(stringResource(R.string.password) + " ${stringResource(R.string.required_field)}") },
                        placeholder = { Text(stringResource(R.string.enter_password)) },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        trailingIcon = {
                            Row {
                                if (uiState.password.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(uiState.password))
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.ContentCopy,
                                            contentDescription = stringResource(R.string.copy_password_description)
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { passwordVisible = !passwordVisible }
                                ) {
                                    Icon(
                                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible)
                                            stringResource(R.string.hide_password)
                                        else
                                            stringResource(R.string.show_password)
                                    )
                                }
                            }
                        },
                        isError = !uiState.isPasswordValid,
                        supportingText = if (!uiState.isPasswordValid) {
                            { Text(stringResource(R.string.password_required)) }
                        } else null
                    )

                    // AI Password Generator Section
                    if (uiState.showPasswordGenerator) {
                        HorizontalDivider()

                        Column(
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
                        ) {
                            Text(
                                text = "🤖 ${stringResource(R.string.ai_password_generator)}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium
                            )

                            // Model Status
                            when {
                                !uiState.isModelInitialized -> {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Text(
                                            text = stringResource(R.string.initializing_ai_model),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                uiState.isGenerating -> {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Text(
                                            text = stringResource(R.string.generating_password),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // Password Pattern Prompt
                            OutlinedTextField(
                                value = uiState.prompt,
                                onValueChange = viewModel::onPromptChanged,
                                label = { Text(stringResource(R.string.password_pattern_prompt)) },
                                placeholder = { Text(stringResource(R.string.password_pattern_placeholder)) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                maxLines = 6,
                                isError = !uiState.isPromptValid,
                                supportingText = if (!uiState.isPromptValid) {
                                    { Text(stringResource(R.string.pattern_prompt_required)) }
                                } else null
                            )

                            // Generation Controls
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (uiState.isGenerating) {
                                    FilledTonalButton(
                                        onClick = viewModel::onStopGeneration
                                    ) {
                                        Text(stringResource(R.string.stop))
                                    }
                                } else {
                                    Button(
                                        onClick = viewModel::onGeneratePassword,
                                        enabled = uiState.canGenerate
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(stringResource(R.string.generate))
                                    }
                                }

                                OutlinedButton(
                                    onClick = viewModel::onUseDefaultPrompt,
                                    enabled = !uiState.isGenerating
                                ) {
                                    Text(stringResource(R.string.use_default))
                                }
                            }

                            Text(
                                text = "💡 ${stringResource(R.string.ai_generation_hint)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Error Display
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(R.dimen.spacing_medium)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = viewModel::clearError) {
                            Text(stringResource(R.string.dismiss))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    viewModel.onPasswordChanged(uiState.password)
                    viewModel.onSaveCredential()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.serviceName.isNotBlank() &&
                         uiState.username.isNotBlank() &&
                         uiState.password.isNotBlank() &&
                         !uiState.isGenerating
            ) {
                Text(
                    if (uiState.isEditing)
                        stringResource(R.string.save_changes)
                    else
                        stringResource(R.string.save_credential)
                )
            }
        }
    }
}
