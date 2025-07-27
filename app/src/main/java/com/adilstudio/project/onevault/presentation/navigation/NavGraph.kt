package com.adilstudio.project.onevault.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.adilstudio.project.onevault.presentation.bill.AddBillScreen
import com.adilstudio.project.onevault.presentation.credential.AddCredentialScreen
import com.adilstudio.project.onevault.presentation.credential.CredentialListScreen
import com.adilstudio.project.onevault.presentation.bill.BillListScreen
import com.adilstudio.project.onevault.presentation.bill.category.BillCategoriesScreen
import org.koin.androidx.compose.koinViewModel
import com.adilstudio.project.onevault.presentation.credential.PasswordManagerViewModel
import com.adilstudio.project.onevault.presentation.filevault.FileVaultScreen
import com.adilstudio.project.onevault.presentation.bill.BillTrackerViewModel

sealed class Screen(val route: String) {
    object BillList : Screen("bill_list")
    object AddBill : Screen("add_bill")
    object BillCategories : Screen("bill_categories")
    object CredentialList : Screen("credential_list")
    object AddCredential : Screen("add_credential")
    object VaultFileList : Screen("vault_file_list")
    object AddVaultFile : Screen("add_vault_file")
    object FileVault : Screen("file_vault")
    object ImportExport : Screen("import_export")
}

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = Screen.BillList.route, modifier = modifier) {
        composable(Screen.BillList.route) {
            BillListScreen(
                onAddBill = { navController.navigate(Screen.AddBill.route) },
                onManageCategories = { navController.navigate(Screen.BillCategories.route) }
            )
        }
        composable(Screen.AddBill.route) {
            val viewModel: BillTrackerViewModel = koinViewModel()
            AddBillScreen(
                viewModel = viewModel,
                onBillAdded = { navController.popBackStack() }
            )
        }
        composable(Screen.CredentialList.route) {
            CredentialListScreen(
                viewModel = koinViewModel(),
                onAddCredential = { navController.navigate(Screen.AddCredential.route) }
            )
        }
        composable(Screen.AddCredential.route) {
            val viewModel: PasswordManagerViewModel = koinViewModel()
            AddCredentialScreen { service, username, password, category ->
                viewModel.addCredential(
                    com.adilstudio.project.onevault.domain.model.Credential(
                        id = System.currentTimeMillis(),
                        serviceName = service,
                        username = username,
                        encryptedPassword = password,
                        category = category
                    )
                )
                navController.popBackStack()
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
    }
}
