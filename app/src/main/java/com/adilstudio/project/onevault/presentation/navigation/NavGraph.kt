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
import org.koin.androidx.compose.koinViewModel
import com.adilstudio.project.onevault.presentation.credential.PasswordManagerViewModel
import com.adilstudio.project.onevault.presentation.filevault.FileVaultScreen

sealed class Screen(val route: String) {
    object BillList : Screen("bill_list")
    object AddBill : Screen("add_bill")
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
            BillListScreen()
        }
        composable(Screen.AddBill.route) {
            AddBillScreen()
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
    }
}
