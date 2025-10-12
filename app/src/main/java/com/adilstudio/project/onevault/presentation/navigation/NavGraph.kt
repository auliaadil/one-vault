package com.adilstudio.project.onevault.presentation.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.adilstudio.project.onevault.presentation.transaction.account.AccountsScreen
import com.adilstudio.project.onevault.presentation.transaction.TransactionFormScreen
import com.adilstudio.project.onevault.presentation.transaction.TransactionListScreen
import com.adilstudio.project.onevault.presentation.transaction.TransactionTrackerViewModel
import com.adilstudio.project.onevault.presentation.transaction.category.TransactionCategoriesScreen
import com.adilstudio.project.onevault.presentation.credential.CredentialListScreen
import com.adilstudio.project.onevault.presentation.credential.CredentialListViewModel
import com.adilstudio.project.onevault.presentation.filevault.FileVaultScreen
import com.adilstudio.project.onevault.presentation.credential.credentialform.CredentialFormScreen
import com.adilstudio.project.onevault.presentation.settings.AboutScreen
import com.adilstudio.project.onevault.presentation.settings.ImportExportScreen
import com.adilstudio.project.onevault.presentation.settings.PrivacyPolicyScreen
import com.adilstudio.project.onevault.presentation.settings.SettingsScreen
import com.adilstudio.project.onevault.presentation.home.HomeScreen
import com.adilstudio.project.onevault.presentation.action.ActionBottomSheet
import org.koin.androidx.compose.koinViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Action : Screen("action")
    object TransactionList : Screen("transaction_list")
    object AddTransaction : Screen("add_transaction")
    object EditTransaction : Screen("edit_transaction")
    object TransactionCategories : Screen("transaction_categories")
    object TransactionAccounts : Screen("transaction_accounts")
    object CredentialList : Screen("credential_list")
    object AddCredential : Screen("add_credential")
    object EditCredential : Screen("edit_credential")
    object VaultFileList : Screen("vault_file_list")
    object AddVaultFile : Screen("add_vault_file")
    object FileVault : Screen("file_vault")
    object ImportExport : Screen("import_export")
    object Settings : Screen("settings")
    object About : Screen("about")
    object PrivacyPolicy : Screen("privacy_policy")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Home.route,
    showScanner: Boolean = false
) {
    NavHost(navController, startDestination = startDestination, modifier = modifier) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTransactions = { navController.navigate(Screen.TransactionList.route) },
                onNavigateToCategories = { navController.navigate(Screen.TransactionCategories.route) },
                onNavigateToAccounts = { navController.navigate(Screen.TransactionAccounts.route) },
                onNavigateToCredentials = { navController.navigate(Screen.CredentialList.route) }
            )
        }
        composable(Screen.TransactionList.route) {
            TransactionListScreen(
                onAddTransaction = {
                    // Clear any existing scanned image URI when using regular add button
                    navController.currentBackStackEntry?.savedStateHandle?.remove<Uri>("scannedImageUri")
                    navController.navigate(Screen.AddTransaction.route)
                },
                onManageCategories = { navController.navigate(Screen.TransactionCategories.route) },
                onEditTransaction = { transaction ->
                    // Pass the transaction ID through navigation arguments
                    navController.currentBackStackEntry?.savedStateHandle?.set("transactionId", transaction.id)
                    navController.navigate(Screen.EditTransaction.route)
                },
                onManageAccounts = { navController.navigate(Screen.TransactionAccounts.route) },
                onAddTransactionWithScannedImage = { imageUri ->
                    // Pass scanned image URI through savedStateHandle and navigate to AddTransaction
                    navController.currentBackStackEntry?.savedStateHandle?.set("scannedImageUri", imageUri)
                    navController.navigate(Screen.AddTransaction.route)
                },
                showScannerDialog = showScanner,
            )
        }
        composable(Screen.AddTransaction.route) {
            val viewModel: TransactionTrackerViewModel = koinViewModel()

            // Get scanned image URI from previous screen if available
            val scannedImageUri = navController.previousBackStackEntry?.savedStateHandle?.get<Uri>("scannedImageUri")

            // Clear the scanned image URI after retrieving it to prevent reuse
            LaunchedEffect(scannedImageUri) {
                if (scannedImageUri != null) {
                    navController.previousBackStackEntry?.savedStateHandle?.remove<Uri>("scannedImageUri")
                }
            }

            TransactionFormScreen(
                scannedImageUri = scannedImageUri,
                onSave = { transaction ->
                    viewModel.addTransaction(transaction)
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.EditTransaction.route) {
            val viewModel: TransactionTrackerViewModel = koinViewModel()
            // Get transaction ID from previous screen
            val transactionId = navController.previousBackStackEntry?.savedStateHandle?.get<Long>("transactionId")

            // Load transactions and find the specific one
            LaunchedEffect(transactionId) {
                viewModel.loadTransactions()
            }

            val transactions by viewModel.transactions.collectAsState()
            val transaction = remember(transactions, transactionId) {
                transactions.find { it.id == transactionId }
            }

            // Show loading while transaction is being fetched
            if (transaction == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                TransactionFormScreen(
                    transaction = transaction,
                    onSave = { updatedTransaction ->
                        viewModel.updateTransaction(updatedTransaction)
                        navController.popBackStack()
                    },
                    onDelete = { transactionId ->
                        viewModel.deleteTransaction(transactionId)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
        }
        composable(Screen.CredentialList.route) {
            CredentialListScreen(
                viewModel = koinViewModel(),
                onAddCredential = { navController.navigate(Screen.AddCredential.route) },
                onEditCredential = { credential ->
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "credentialId",
                        credential.id
                    )
                    navController.navigate(Screen.EditCredential.route)
                }
            )
        }
        composable(Screen.AddCredential.route) {
            CredentialFormScreen(
                credential = null, // For new credentials
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.EditCredential.route) {
            val viewModel: CredentialListViewModel = koinViewModel()
            // Get credential ID from previous screen
            val credentialId =
                navController.previousBackStackEntry?.savedStateHandle?.get<Long>("credentialId")

            // Load credentials and find the specific one
            LaunchedEffect(credentialId) {
                viewModel.loadCredentials()
            }

            val credentials by viewModel.credentials.collectAsState()
            val credential = remember(credentials, credentialId) {
                credentials.find { it.id == credentialId }
            }

            // Show loading while credential is being fetched
            if (credential == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                CredentialFormScreen(
                    credential = credential,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
        composable(Screen.VaultFileList.route) {
            FileVaultScreen(
                context = LocalContext.current,
                onPickFile = { onResult -> /* TODO: Launch OpenDocument and call onResult(uri) */ },
                onImport = { onResult -> /* TODO: Launch OpenDocument and call onResult(uri) */ },
                onExport = { onResult -> /* TODO: Launch CreateDocument and call onResult(uri) */ }
            )
        }
        composable(Screen.AddVaultFile.route) {
            // AddVaultFileScreen()
        }
        composable(Screen.FileVault.route) {
            // You will need to pass the correct context and lambdas for file pick/import/export
            val context = LocalContext.current
            FileVaultScreen(
                context = context,
                onPickFile = { onResult -> /* TODO: Launch OpenDocument and call onResult(uri) */ },
                onImport = { onResult -> /* TODO: Launch OpenDocument and call onResult(uri) */ },
                onExport = { onResult -> /* TODO: Launch CreateDocument and call onResult(uri) */ }
            )
        }
        composable(Screen.TransactionCategories.route) {
            TransactionCategoriesScreen()
        }
        composable(Screen.TransactionAccounts.route) {
            AccountsScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateToAbout = { navController.navigate(Screen.About.route) },
                onNavigateToPrivacyPolicy = { navController.navigate(Screen.PrivacyPolicy.route) },
                onNavigateToImportExport = { navController.navigate(Screen.ImportExport.route) }
            )
        }
        composable(Screen.About.route) {
            AboutScreen(
            )
        }
        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(
            )
        }
        composable(Screen.ImportExport.route) {
            ImportExportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Action.route) {
            ActionBottomSheet(
                onDismiss = { navController.popBackStack() }
            )
        }
    }
}
