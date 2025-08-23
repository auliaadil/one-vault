package com.adilstudio.project.onevault.presentation.passwordgenerator

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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.adilstudio.project.onevault.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordGeneratorScreen(
    onNavigateBack: () -> Unit,
    viewModel: PasswordGeneratorViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.ai_password_generator))
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
        PasswordGeneratorContent(
            uiState = uiState,
            onServiceNameChanged = viewModel::onServiceNameChanged,
            onUserNameChanged = viewModel::onUserNameChanged,
            onPromptChanged = viewModel::onPromptChanged,
            onGeneratePassword = viewModel::onGeneratePassword,
            onSavePassword = viewModel::onSavePassword,
            onStopGeneration = viewModel::onStopGeneration,
            onUseDefaultPrompt = viewModel::onUseDefaultPrompt,
            onCopyPassword = { password ->
                clipboardManager.setText(AnnotatedString(password))
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
fun PasswordGeneratorContent(
    uiState: PasswordGeneratorUiState,
    onServiceNameChanged: (String) -> Unit,
    onUserNameChanged: (String) -> Unit,
    onPromptChanged: (String) -> Unit,
    onGeneratePassword: () -> Unit,
    onSavePassword: () -> Unit,
    onStopGeneration: () -> Unit,
    onUseDefaultPrompt: () -> Unit,
    onCopyPassword: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(dimensionResource(R.dimen.spacing_large)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_large))
    ) {
        // Step 1: Service Information
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_medium))
        ) {
            Column(
                modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
            ) {
                Text(
                    text = stringResource(R.string.step_1_service_information),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = uiState.serviceName,
                    onValueChange = onServiceNameChanged,
                    label = { Text(stringResource(R.string.service_name_label)) },
                    placeholder = { Text(stringResource(R.string.service_name_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !uiState.isServiceNameValid,
                    supportingText = if (!uiState.isServiceNameValid) {
                        { Text(stringResource(R.string.service_name_required)) }
                    } else null
                )

                OutlinedTextField(
                    value = uiState.userName,
                    onValueChange = onUserNameChanged,
                    label = { Text(stringResource(R.string.username_email_label)) },
                    placeholder = { Text(stringResource(R.string.username_email_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !uiState.isUserNameValid,
                    supportingText = if (!uiState.isUserNameValid) {
                        { Text(stringResource(R.string.username_required)) }
                    } else null
                )
            }
        }

        // Step 2: Pattern Prompt
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_medium))
        ) {
            Column(
                modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.step_2_password_pattern),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedButton(
                        onClick = onUseDefaultPrompt,
                        enabled = !uiState.isGenerating
                    ) {
                        Text(stringResource(R.string.use_default))
                    }
                }

                Text(
                    text = "💡 ${stringResource(R.string.default_prompt_hint)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = uiState.prompt,
                    onValueChange = onPromptChanged,
                    label = { Text(stringResource(R.string.password_pattern_prompt)) },
                    placeholder = {
                        Text(stringResource(R.string.password_pattern_placeholder))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 8,
                    isError = !uiState.isPromptValid,
                    supportingText = if (!uiState.isPromptValid) {
                        { Text(stringResource(R.string.pattern_prompt_required)) }
                    } else null
                )
            }
        }

        // Step 3: Generation
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_medium))
        ) {
            Column(
                modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
            ) {
                Text(
                    text = stringResource(R.string.step_3_generate_password_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Status indicators
                when {
                    !uiState.isInitialized -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium)),
                                strokeWidth = dimensionResource(R.dimen.stroke_width_medium)
                            )
                            Text(
                                text = stringResource(R.string.initializing_ai_model),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    uiState.isGenerating -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(dimensionResource(R.dimen.icon_size_medium)),
                                strokeWidth = dimensionResource(R.dimen.stroke_width_medium)
                            )
                            Text(
                                text = stringResource(R.string.generating_password),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
                ) {
                    if (uiState.isGenerating) {
                        FilledTonalButton(
                            onClick = onStopGeneration
                        ) {
                            Text(stringResource(R.string.stop))
                        }
                    } else {
                        Button(
                            onClick = onGeneratePassword,
                            enabled = uiState.canGenerate
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
                            Text(stringResource(R.string.generate))
                        }
                    }
                }

                // Generated password display
                if (uiState.generatedPassword.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large)),
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
                        ) {
                            Text(
                                text = stringResource(R.string.generated_password_colon),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = uiState.generatedPassword,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.weight(1f)
                                )

                                IconButton(
                                    onClick = { onCopyPassword(uiState.generatedPassword) }
                                ) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = stringResource(R.string.copy_password_description),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Step 4: Save
        if (uiState.canSave) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_medium))
            ) {
                Column(
                    modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
                ) {
                    Text(
                        text = stringResource(R.string.step_4_save_password_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stringResource(R.string.save_password_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = onSavePassword,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
                        Text(stringResource(R.string.save_password_and_pattern))
                    }
                }
            }
        }

        // Error display
        uiState.error?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large)),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordGeneratorScreenPreview() {
    MaterialTheme {
        PasswordGeneratorContent(
            uiState = PasswordGeneratorUiState(
                serviceName = "Bitbucket",
                userName = "aulia@gmail.com",
                prompt = """Develop a password with pattern:
- Use characters from service name: {serviceName}
- Use characters from username: {userName}
- Add default suffix 123!""",
                generatedPassword = "AuliBit123!",
                isGenerating = false,
                isInitialized = true,
                canGenerate = true,
                canSave = true
            ),
            onServiceNameChanged = {},
            onUserNameChanged = {},
            onPromptChanged = {},
            onGeneratePassword = {},
            onSavePassword = {},
            onStopGeneration = {},
            onUseDefaultPrompt = {},
            onCopyPassword = {}
        )
    }
}
