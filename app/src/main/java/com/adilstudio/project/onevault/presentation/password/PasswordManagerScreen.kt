package com.adilstudio.project.onevault.presentation.password

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.domain.model.Credential
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordManagerScreen(
    credential: Credential? = null, // For editing existing credentials
    onNavigateBack: () -> Unit = {},
    viewModel: PasswordViewModel = koinViewModel()
) {
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
                        stringResource(
                            if (credential != null) R.string.edit_credential_title
                            else R.string.add_credential_title
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
                .padding(horizontal = dimensionResource(R.dimen.spacing_large))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_large))
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

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
                            .padding(dimensionResource(R.dimen.spacing_medium)),
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
                            Text(stringResource(R.string.dismiss))
                        }
                    }
                }
            }

            // Service Name Input
            OutlinedTextField(
                value = serviceName,
                onValueChange = viewModel::updateServiceName,
                label = { Text(stringResource(R.string.service_name)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.service_name_placeholder)) },
                enabled = !isLoading
            )

            // User Account Input
            OutlinedTextField(
                value = userAccount,
                onValueChange = viewModel::updateUserAccount,
                label = { Text(stringResource(R.string.username_email)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.username_email_placeholder)) },
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
                    modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
                ) {
                    // Toggle switch for template usage
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.generate_from_template),
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
                            text = stringResource(R.string.configure_rules_message),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        // Manual mode - show password input
                        OutlinedTextField(
                            value = password,
                            onValueChange = viewModel::updatePassword,
                            label = { Text(stringResource(R.string.password)) },
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = stringResource(if (showPassword) R.string.hide_password else R.string.show_password)
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
                            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large))
                        ) {
                            Text(
                                text = stringResource(R.string.password_rules_drag_hint),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(bottom = dimensionResource(R.dimen.spacing_medium))
                            )

                            LazyColumn(
                                state = reorderableState.listState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(dimensionResource(R.dimen.list_height_fixed)) // Fixed height for better UX
                                    .reorderable(reorderableState),
                                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs))
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
                                            modifier = Modifier.detectReorderAfterLongPress(
                                                reorderableState
                                            )
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
                        modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large))
                    ) {
                        Text(
                            text = stringResource(R.string.generated_password),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(bottom = dimensionResource(R.dimen.spacing_small))
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
                                        contentDescription = stringResource(if (showPassword) R.string.hide_password else R.string.show_password)
                                    )
                                }
                            },
                            placeholder = {
                                Text(
                                    text = stringResource(
                                        if (viewModel.rules.isEmpty())
                                            R.string.add_rules_to_generate
                                        else
                                            R.string.password_will_appear_here
                                    ),
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.6f)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(
                                    alpha = 0.6f
                                ),
                                focusedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(
                                    alpha = 0.6f
                                )
                            )
                        )

                        if (generatedPassword.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
                            Text(
                                text = stringResource(R.string.password_length_format, generatedPassword.length),
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
                            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_medium))
                        ) {
                            Text(
                                text = stringResource(R.string.live_preview),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )

                            if (serviceName.isNotEmpty()) {
                                Text(
                                    text = stringResource(R.string.service_preview_format, serviceName),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }

                            if (userAccount.isNotEmpty()) {
                                Text(
                                    text = stringResource(R.string.account_preview_format, userAccount),
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
                        modifier = Modifier.size(dimensionResource(R.dimen.spacing_large)),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
                }
                Text(
                    stringResource(
                        if (credential != null) R.string.update_credential
                        else R.string.save_credential
                    )
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))
        }
    }
}
