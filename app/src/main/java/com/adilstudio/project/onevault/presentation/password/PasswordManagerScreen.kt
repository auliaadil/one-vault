package com.adilstudio.project.onevault.presentation.password

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.domain.model.Credential
import org.burnoutcrew.reorderable.*
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordManagerScreen(
    credential: Credential? = null, // For editing existing credentials
    onNavigateBack: () -> Unit = {},
    viewModel: PasswordViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val serviceName by viewModel.serviceName.collectAsState()
    val userAccount by viewModel.userAccount.collectAsState()
    val password by viewModel.password.collectAsState()
    val useTemplate by viewModel.useTemplate.collectAsState()
    val generatedPassword by viewModel.generatedPassword.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var showPassword by remember { mutableStateOf(false) }

    // Load existing credential if provided
    LaunchedEffect(credential) {
        credential?.let { viewModel.loadCredential(it) }
    }

    // Handle success message
    LaunchedEffect(successMessage) {
        successMessage?.let {
            // Navigate back on successful save
            onNavigateBack()
            viewModel.clearSuccessMessage()
        }
    }

    // Reorderable state for drag and drop
    val reorderableState = rememberReorderableLazyListState(
        onMove = { from, to ->
            viewModel.moveRule(from.index, to.index)
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (credential != null) "Edit Credential"
                        else "Add Credential"
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Error message
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = { viewModel.clearError() }
                        ) {
                            Text("Dismiss")
                        }
                    }
                }
            }

            // Service Name Input
            OutlinedTextField(
                value = serviceName,
                onValueChange = viewModel::updateServiceName,
                label = { Text("Service Name") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g., Google, Facebook, GitHub") },
                enabled = !isLoading
            )

            // User Account Input
            OutlinedTextField(
                value = userAccount,
                onValueChange = viewModel::updateUserAccount,
                label = { Text("Username / Email") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g., john.doe, myemail@gmail.com") },
                enabled = !isLoading
            )

            // Password Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Toggle switch for template usage
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Generate from template",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Switch(
                            checked = useTemplate,
                            onCheckedChange = { viewModel.toggleUseTemplate() },
                            enabled = !isLoading
                        )
                    }

                    if (useTemplate) {
                        // Template mode - show rules and generated password
                        Text(
                            text = "Configure rules to automatically generate your password:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        // Manual mode - show password input
                        OutlinedTextField(
                            value = password,
                            onValueChange = viewModel::updatePassword,
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (showPassword) "Hide password" else "Show password"
                                    )
                                }
                            },
                            enabled = !isLoading
                        )
                    }
                }
            }

            // Template Configuration Section
            if (useTemplate) {
                // Add Rule Section
                AddRuleSection(
                    onAddRule = viewModel::addRule,
                    modifier = Modifier.fillMaxWidth()
                )

                // Rules List with Drag and Drop
                if (viewModel.rules.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Password Rules (drag to reorder)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            LazyColumn(
                                state = reorderableState.listState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp) // Fixed height for better UX
                                    .reorderable(reorderableState),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                itemsIndexed(
                                    items = viewModel.rules,
                                    key = { _, rule -> rule.id }
                                ) { index, rule ->
                                    ReorderableItem(
                                        reorderableState = reorderableState,
                                        key = rule.id
                                    ) { isDragging ->
                                        RuleItem(
                                            rule = rule,
                                            isDragging = isDragging,
                                            onRuleUpdate = viewModel::updateRule,
                                            onRuleDelete = viewModel::removeRule,
                                            modifier = Modifier.detectReorderAfterLongPress(reorderableState)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Generated Password Preview
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Generated Password",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = generatedPassword,
                            onValueChange = { },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (showPassword) "Hide password" else "Show password"
                                    )
                                }
                            },
                            placeholder = {
                                Text(
                                    text = if (viewModel.rules.isEmpty())
                                        "Add rules to generate password"
                                    else
                                        "Password will appear here",
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f),
                                focusedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f)
                            )
                        )

                        if (generatedPassword.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Length: ${generatedPassword.length} characters",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Live Preview Info
                if (serviceName.isNotEmpty() || userAccount.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "Live Preview",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )

                            if (serviceName.isNotEmpty()) {
                                Text(
                                    text = "Service: \"$serviceName\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }

                            if (userAccount.isNotEmpty()) {
                                Text(
                                    text = "Account: \"$userAccount\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // Save Button
            Button(
                onClick = { viewModel.saveCredential() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && serviceName.isNotBlank() && userAccount.isNotBlank() &&
                         ((useTemplate && generatedPassword.isNotBlank()) || (!useTemplate && password.isNotBlank()))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    if (credential != null) "Update Credential"
                    else "Save Credential"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
