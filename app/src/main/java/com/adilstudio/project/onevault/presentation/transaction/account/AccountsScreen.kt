package com.adilstudio.project.onevault.presentation.transaction.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.tooling.preview.Preview
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.core.util.RupiahFormatter
import com.adilstudio.project.onevault.domain.model.Account
import com.adilstudio.project.onevault.presentation.component.EmptyState
import com.adilstudio.project.onevault.presentation.component.BaseScreen
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreen(
    viewModel: AccountViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val accounts by viewModel.accounts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingAccount by remember { mutableStateOf<Account?>(null) }
    var accountToDelete by remember { mutableStateOf<Account?>(null) }

    // Handle error messages
    LaunchedEffect(error) {
        error?.let {
            viewModel.clearError()
        }
    }

    // Handle success messages
    LaunchedEffect(successMessage) {
        successMessage?.let {
            viewModel.clearSuccessMessage()
        }
    }

    BaseScreen(
        title = stringResource(R.string.accounts),
        showNavIcon = true,
        successMessage = successMessage,
        errorMessage = error,
        onClearSuccess = { viewModel.clearSuccessMessage() },
        onClearError = { viewModel.clearError() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_account))
            }
        },
        defaultPaddingHorizontal = R.dimen.spacing_none
    ) { paddingValues ->
        if (accounts.isEmpty()) {
            EmptyState(
                title = stringResource(R.string.no_accounts_saved),
                description = stringResource(R.string.tap_plus_to_add_first_account),
                modifier = Modifier.padding(paddingValues)
            )
        } else if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.spacing_large)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
            ) {
                items(accounts) { account ->
                    AccountCard(
                        account = account,
                        onEdit = { editingAccount = it },
                        onDelete = { accountToDelete = it }
                    )
                }
            }
        }
    }

    // Add/Edit Account Dialog
    if (showAddDialog || editingAccount != null) {
        AddEditAccountDialog(
            account = editingAccount,
            onDismiss = {
                showAddDialog = false
                editingAccount = null
            },
            onSave = { name, amount, description ->
                if (editingAccount != null) {
                    viewModel.updateAccount(
                        editingAccount!!.copy(
                            name = name,
                            amount = amount,
                            description = description.takeIf { it.isNotBlank() },
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                } else {
                    viewModel.addAccount(name, amount, description)
                }
                showAddDialog = false
                editingAccount = null
            }
        )
    }

    // Delete Confirmation Dialog
    accountToDelete?.let { account ->
        AlertDialog(
            onDismissRequest = { accountToDelete = null },
            title = { Text(stringResource(R.string.delete_account)) },
            text = { Text(stringResource(R.string.delete_account_message, account.name)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAccount(account.id)
                        accountToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { accountToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountCard(
    account: Account,
    onEdit: (Account) -> Unit,
    onDelete: (Account) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.elevation_small))
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = RupiahFormatter.formatWithRupiahPrefix(account.amount.toLong()),
                        style = MaterialTheme.typography.titleLarge,
                        color = if (account.amount >= 0)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    account.description?.let { desc ->
                        if (desc.isNotBlank()) {
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Action buttons
                Row {
                    if (account.isEditable) {
                        IconButton(onClick = { onEdit(account) }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit)
                            )
                        }
                        IconButton(onClick = { onDelete(account) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountCardPreview() {
    MaterialTheme {
        AccountCard(
            account = Account(
                id = 1L,
                name = "BCA Savings",
                amount = 5000000.0,
                description = "Primary savings account"
            ),
            onEdit = {},
            onDelete = {}
        )
    }
}
