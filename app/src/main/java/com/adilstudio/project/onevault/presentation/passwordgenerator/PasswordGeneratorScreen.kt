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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step 1: Service Information
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Step 1: Service Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = uiState.serviceName,
                    onValueChange = onServiceNameChanged,
                    label = { Text("Service Name") },
                    placeholder = { Text("e.g., Bitbucket, GitHub, Gmail") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !uiState.isServiceNameValid,
                    supportingText = if (!uiState.isServiceNameValid) {
                        { Text("Service name is required") }
                    } else null
                )

                OutlinedTextField(
                    value = uiState.userName,
                    onValueChange = onUserNameChanged,
                    label = { Text("Username/Email") },
                    placeholder = { Text("e.g., aulia@gmail.com") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !uiState.isUserNameValid,
                    supportingText = if (!uiState.isUserNameValid) {
                        { Text("Username is required") }
                    } else null
                )
            }
        }

        // Step 2: Pattern Prompt
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Step 2: Password Pattern",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedButton(
                        onClick = onUseDefaultPrompt,
                        enabled = !uiState.isGenerating
                    ) {
                        Text("Use Default")
                    }
                }

                Text(
                    text = "💡 This prompt will be saved as your default for future password generation",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = uiState.prompt,
                    onValueChange = onPromptChanged,
                    label = { Text("Password Pattern Prompt") },
                    placeholder = {
                        Text("""Develop a password with pattern:
- Use characters from service name
- Use characters from username  
- Add default suffix 123!""")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 8,
                    isError = !uiState.isPromptValid,
                    supportingText = if (!uiState.isPromptValid) {
                        { Text("Pattern prompt is required") }
                    } else null
                )
            }
        }

        // Step 3: Generation
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Step 3: Generate Password",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Status indicators
                when {
                    !uiState.isInitialized -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Initializing AI model...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    uiState.isGenerating -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Generating password...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (uiState.isGenerating) {
                        FilledTonalButton(
                            onClick = onStopGeneration
                        ) {
                            Text("Stop")
                        }
                    } else {
                        Button(
                            onClick = onGeneratePassword,
                            enabled = uiState.canGenerate
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate")
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
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Generated Password:",
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
                                        contentDescription = "Copy password",
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
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Step 4: Save Password",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Save this password and pattern for future use. The pattern will become your default for generating similar passwords.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = onSavePassword,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save Password & Pattern")
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
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
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
