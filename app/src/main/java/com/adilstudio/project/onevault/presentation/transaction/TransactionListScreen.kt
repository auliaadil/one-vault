package com.adilstudio.project.onevault.presentation.transaction

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import com.adilstudio.project.onevault.core.util.CurrencyFormatter
import com.adilstudio.project.onevault.core.util.DateUtil
import com.adilstudio.project.onevault.core.util.RupiahFormatter
import com.adilstudio.project.onevault.domain.model.Transaction
import com.adilstudio.project.onevault.domain.model.TransactionCategory
import com.adilstudio.project.onevault.domain.model.CategoryType
import com.adilstudio.project.onevault.domain.model.Currency
import com.adilstudio.project.onevault.domain.model.TransactionType
import com.adilstudio.project.onevault.presentation.component.EmptyState
import com.adilstudio.project.onevault.presentation.component.BaseScreen
import com.adilstudio.project.onevault.ui.theme.OneVaultTheme
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    transactionTrackerViewModel: TransactionTrackerViewModel = koinViewModel(),
    onAddTransaction: () -> Unit = {},
    onManageCategories: () -> Unit = {},
    onEditTransaction: (Transaction) -> Unit = {},
    onManageAccounts: () -> Unit = {},
    onAddTransactionWithScannedImage: (Uri) -> Unit = {},
    showScannerDialog: Boolean = false
) {
    val transactions = transactionTrackerViewModel.transactions.collectAsState().value
    val categories = transactionTrackerViewModel.categories.collectAsState().value
    val accounts = transactionTrackerViewModel.accounts.collectAsState().value

    // Scanner dialog state
    var showScannerDialogState by remember { mutableStateOf(showScannerDialog) }

    // Bottom sheet state
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    var showTransactionDetailSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        transactionTrackerViewModel.loadTransactions()
    }

    BaseScreen(
        title = stringResource(R.string.transactions),
        showNavIcon = true,
        actions = {
            OutlinedButton(onClick = onManageCategories) {
                Icon(
                    Icons.Default.Category,
                    contentDescription = stringResource(R.string.manage_categories)
                )
            }
            OutlinedButton(onClick = onManageAccounts) {
                Icon(
                    Icons.Default.AccountBalance,
                    contentDescription = stringResource(R.string.accounts)
                )
            }
        },
        floatingActionButton = {
            Column {
                FloatingActionButton(onClick = { showScannerDialogState = true }) {
                    Icon(
                        Icons.Default.DocumentScanner,
                        contentDescription = stringResource(R.string.scan_transaction)
                    )
                }
                Spacer(modifier = Modifier.size(dimensionResource(R.dimen.spacing_small)))
                FloatingActionButton(onClick = onAddTransaction) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_transaction)
                    )
                }
            }
        },
    ) { paddingValues ->
        TransactionListContent(
            transactions = transactions,
            categories = categories,
            onViewDetail = { transaction ->
                selectedTransaction = transaction
                showTransactionDetailSheet = true
            },
            modifier = Modifier
        )
    }

    // Transaction Detail Bottom Sheet
    if (showTransactionDetailSheet && selectedTransaction != null) {
        TransactionDetailBottomSheet(
            transaction = selectedTransaction!!,
            onDismiss = {
                showTransactionDetailSheet = false
                selectedTransaction = null
            },
            onEdit = { transaction ->
                showTransactionDetailSheet = false
                selectedTransaction = null
                onEditTransaction(transaction)
            },
            onDelete = { transactionId ->
                showTransactionDetailSheet = false
                selectedTransaction = null
                transactionTrackerViewModel.deleteTransaction(transactionId)
            },
            categories = categories,
            accounts = accounts
        )
    }

    // Scanner Dialog
    if (showScannerDialogState) {
        TransactionScannerDialog(
            onDismiss = { showScannerDialogState = false },
            onImageCaptured = { imageUri ->
                showScannerDialogState = false
                onAddTransactionWithScannedImage(imageUri)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionCard(
    transaction: Transaction,
    categories: List<TransactionCategory>,
    onClick: () -> Unit
) {
    val category = transaction.categoryId?.let { id ->
        categories.find { it.id == id }
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.spacing_xs))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacing_large)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val formattedDate = DateUtil.isoStringToLocalDate(transaction.date)?.let { date ->
                    DateUtil.formatDateForDisplay(date)
                } ?: transaction.date
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(transaction.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = CurrencyFormatter.formatWithPrefix(
                        transaction.amount.toLong(),
                        Currency.current
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    color = if (transaction.amount >= 0)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
            // Category icon + name at top right, max width 80dp, max 2 lines
            Text(
                text = category?.getIconAndName() ?: stringResource(R.string.no_category),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun TransactionListContent(
    transactions: List<Transaction>,
    categories: List<TransactionCategory>,
    onViewDetail: (Transaction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if (transactions.isEmpty()) {
            EmptyState(
                title = stringResource(R.string.no_transactions_saved),
                description = stringResource(R.string.tap_plus_to_add_first_transaction),
                modifier = modifier
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.spacing_large)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
            ) {
                items(transactions.size) { idx ->
                    val transaction = transactions[idx]
                    TransactionCard(
                        transaction = transaction,
                        categories = categories,
                        onClick = { onViewDetail(transaction) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionListScreenPreview() {
    // Create mock categories for preview
    val currentTime = System.currentTimeMillis()
    val mockCategories = listOf(
        TransactionCategory(
            id = 1,
            name = "Gas",
            icon = "🔥",
            color = "#FF5722",
            type = CategoryType.UTILITIES,
            transactionType = TransactionType.EXPENSE,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        TransactionCategory(
            id = 2,
            name = "Groceries",
            icon = "🛒",
            color = "#8BC34A",
            type = CategoryType.FOOD_AND_DINING,
            transactionType = TransactionType.EXPENSE,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        TransactionCategory(
            id = 3,
            name = "Dining Out",
            icon = "🍽️",
            color = "#FF9800",
            type = CategoryType.FOOD_AND_DINING,
            transactionType = TransactionType.EXPENSE,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        TransactionCategory(
            id = 4,
            name = "Coffee & Beverages",
            icon = "☕",
            color = "#8D6E63",
            type = CategoryType.FOOD_AND_DINING,
            transactionType = TransactionType.EXPENSE,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
        TransactionCategory(
            id = 5,
            name = "School Fees",
            icon = "🎓",
            color = "#2196F3",
            type = CategoryType.EDUCATION,
            transactionType = TransactionType.EXPENSE,
            isEditable = false,
            createdAt = currentTime,
            updatedAt = currentTime
        ),
    )
    val mockTransactions = listOf(
        Transaction(
            id = 1,
            title = "Electric Transaction",
            merchant = "PLN", // Updated from vendor
            amount = 500000.0,
            date = "2024-01-15", // Updated from transactionDate
            type = TransactionType.EXPENSE, // New field
            categoryId = 1,
            imagePath = null
        ),
        Transaction(
            id = 2,
            title = "Internet Transaction",
            merchant = "Telkom", // Updated from vendor
            amount = 300000.0,
            date = "2024-01-10", // Updated from transactionDate
            type = TransactionType.EXPENSE, // New field
            categoryId = 2,
            imagePath = "/path/to/image"
        )
    )
    OneVaultTheme(
        darkTheme = true
    ) {
        BaseScreen(
            title = stringResource(R.string.transactions),
            actions = {
                OutlinedButton(onClick = { }) {
                    Icon(
                        Icons.Default.DocumentScanner,
                        contentDescription = stringResource(R.string.scan_transaction)
                    )
                }
                OutlinedButton(onClick = { }) {
                    Icon(
                        Icons.Default.Category,
                        contentDescription = stringResource(R.string.manage_categories)
                    )
                }
                OutlinedButton(onClick = {}) {
                    Icon(
                        Icons.Default.AccountBalanceWallet,
                        contentDescription = stringResource(R.string.accounts)
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {}) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_transaction)
                    )
                }
            },
        ) {
            TransactionListContent(
                transactions = mockTransactions,
                categories = mockCategories,
                onViewDetail = {}
            )
        }
    }
}
