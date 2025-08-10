package com.adilstudio.project.onevault.presentation.navigation

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
import com.adilstudio.project.onevault.presentation.bill.AddEditBillScreen
import com.adilstudio.project.onevault.presentation.bill.BillListScreen
import com.adilstudio.project.onevault.presentation.bill.BillTrackerViewModel
import com.adilstudio.project.onevault.presentation.bill.category.BillCategoriesScreen
import com.adilstudio.project.onevault.presentation.credential.AddEditCredentialScreen
import com.adilstudio.project.onevault.presentation.credential.CredentialListScreen
import com.adilstudio.project.onevault.presentation.credential.PasswordManagerViewModel
import com.adilstudio.project.onevault.presentation.filevault.FileVaultScreen
import com.adilstudio.project.onevault.presentation.gpt2.GPT2Screen
import com.adilstudio.project.onevault.presentation.gpt2.GPT2ViewModel
import com.adilstudio.project.onevault.presentation.settings.AboutScreen
import com.adilstudio.project.onevault.presentation.settings.ImportExportScreen
import com.adilstudio.project.onevault.presentation.settings.PrivacyPolicyScreen
import com.adilstudio.project.onevault.presentation.settings.SettingsScreen
import org.koin.androidx.compose.koinViewModel

sealed class Screen(val route: String) {
    object BillList : Screen("bill_list")
    object AddBill : Screen("add_bill")
    object EditBill : Screen("edit_bill")
    object BillCategories : Screen("bill_categories")
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
    object LLMChat : Screen("llm_chat")
    object LLMChatSettings : Screen("llm_chat_settings")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.BillList.route
) {
    NavHost(navController, startDestination = startDestination, modifier = modifier) {
        composable(Screen.BillList.route) {
            BillListScreen(
                onAddBill = { navController.navigate(Screen.AddBill.route) },
                onManageCategories = { navController.navigate(Screen.BillCategories.route) },
                onEditBill = { bill ->
                    // Pass the bill ID through navigation arguments
                    navController.currentBackStackEntry?.savedStateHandle?.set("billId", bill.id)
                    navController.navigate(Screen.EditBill.route)
                }
            )
        }
        composable(Screen.AddBill.route) {
            val viewModel: BillTrackerViewModel = koinViewModel()
            AddEditBillScreen(
                onSave = { bill ->
                    viewModel.addBill(bill)
                    navController.popBackStack()
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.EditBill.route) {
            val viewModel: BillTrackerViewModel = koinViewModel()
            // Get bill ID from previous screen
            val billId = navController.previousBackStackEntry?.savedStateHandle?.get<Long>("billId")

            // Load bills and find the specific one
            LaunchedEffect(billId) {
                viewModel.loadBills()
            }

            val bills by viewModel.bills.collectAsState()
            val bill = remember(bills, billId) {
                bills.find { it.id == billId }
            }

            // Show loading while bill is being fetched
            if (bill == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                AddEditBillScreen(
                    bill = bill,
                    onSave = { updatedBill ->
                        viewModel.updateBill(updatedBill)
                        navController.popBackStack()
                    },
                    onDelete = { billId ->
                        viewModel.deleteBill(billId)
                        navController.popBackStack()
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }
        }
        composable(Screen.CredentialList.route) {
            CredentialListScreen(
                viewModel = koinViewModel(),
                onAddCredential = { navController.navigate(Screen.AddCredential.route) },
                onEditCredential = { credential ->
                    // Pass only the credential ID through navigation arguments
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "credentialId",
                        credential.id
                    )
                    navController.navigate(Screen.EditCredential.route)
                }
            )
        }
        composable(Screen.AddCredential.route) {
            val viewModel: PasswordManagerViewModel = koinViewModel()
            AddEditCredentialScreen(
                onSave = { service, username, password ->
                    viewModel.addCredential(
                        com.adilstudio.project.onevault.domain.model.Credential(
                            id = System.currentTimeMillis(),
                            serviceName = service,
                            username = username,
                            encryptedPassword = password
                        )
                    )
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.EditCredential.route) {
            val viewModel: PasswordManagerViewModel = koinViewModel()
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
                AddEditCredentialScreen(
                    credential = credential,
                    onSave = { service, username, password ->
                        viewModel.updateCredential(
                            credential.copy(
                                serviceName = service,
                                username = username,
                                encryptedPassword = password
                            )
                        )
                        navController.popBackStack()
                    },
                    onCancel = {
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
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
        composable(Screen.BillCategories.route) {
            BillCategoriesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
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
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.ImportExport.route) {
            ImportExportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.LLMChat.route) {
            val viewModel: GPT2ViewModel = koinViewModel()
            GPT2Screen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}
