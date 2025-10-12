package com.adilstudio.project.onevault

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.adilstudio.project.onevault.domain.manager.AppSecurityManager
import com.adilstudio.project.onevault.domain.manager.BiometricAuthManager
import com.adilstudio.project.onevault.presentation.TopBarViewModel
import com.adilstudio.project.onevault.presentation.action.ActionBottomSheet
import com.adilstudio.project.onevault.presentation.biometric.BiometricLockScreen
import com.adilstudio.project.onevault.presentation.navigation.NavGraph
import com.adilstudio.project.onevault.presentation.navigation.Screen
import com.adilstudio.project.onevault.ui.theme.OneVaultTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

class MainActivity : FragmentActivity() {

    private val appSecurityManager: AppSecurityManager by inject()
    private val biometricAuthManager: BiometricAuthManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before super.onCreate()
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if launched from TileService
        val navigateTo = intent.getStringExtra("navigate_to")
        val showScanner = intent.getBooleanExtra("show_scanner", false)

        val initialRoute = when (navigateTo) {
            Screen.AddTransaction.route -> Screen.AddTransaction.route
            else -> null
        }

        // Only check and lock app on launch
        lifecycleScope.launch {
            if (biometricAuthManager.isBiometricEnabled()) {
                appSecurityManager.checkAndLockOnLaunch()
            }
        }

        setContent {
            MainApp(
                initialRoute = initialRoute,
                showScanner = showScanner,
                appSecurityManager = appSecurityManager,
                onAppExit = { finishAffinity() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    initialRoute: String? = null,
    showScanner: Boolean = false,
    appSecurityManager: AppSecurityManager? = null,
    onAppExit: () -> Unit = {},
    topBarViewModel: TopBarViewModel = koinViewModel()
) {
    // Collect the state from the ViewModel
    val title by topBarViewModel.title.collectAsState()
    val showNavIcon by topBarViewModel.showNavigationIcon.collectAsState()
    val actions by topBarViewModel.actions.collectAsState()

    OneVaultTheme {
        val navController = rememberNavController()
        val isAppLocked by (appSecurityManager?.isAppLocked?.collectAsState() ?: remember { mutableStateOf(false) })

        // State for action bottom sheet
        var showActionSheet by remember { mutableStateOf(false) }

        // Show biometric lock screen if app is locked
        if (isAppLocked) {
            BiometricLockScreen(
                onAuthenticationSuccess = {
                    appSecurityManager?.unlockApp()
                },
                onAppExit = onAppExit
            )
        } else {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(title) },
                        navigationIcon = {
                            if (showNavIcon) {
                                IconButton(onClick = { navController.navigateUp() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = stringResource(R.string.back)
                                    )
                                }
                            }
                        },
                        actions = actions
                    )
                },
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    val items = listOf(
                        Triple(stringResource(R.string.home), Screen.Home.route, Icons.Filled.Home),
                        Triple(stringResource(R.string.action), "action_sheet", Icons.Filled.Add),
                        Triple(stringResource(R.string.settings), Screen.Settings.route, Icons.Filled.Settings),
                    )
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    NavigationBar {
                        items.forEach { (label, route, icon) ->
                            NavigationBarItem(
                                icon = { Icon(icon, contentDescription = label) },
                                label = { Text(label) },
                                selected = currentRoute == route || (route == "action_sheet" && showActionSheet),
                                onClick = {
                                    if (route == "action_sheet") {
                                        showActionSheet = true
                                    } else if (currentRoute != route) {
                                        navController.navigate(route) {
                                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                },
            ) { innerPadding ->
                NavGraph(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                    startDestination = initialRoute ?: Screen.Home.route,
                    showScanner = showScanner
                )

                // Action Bottom Sheet
                if (showActionSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showActionSheet = false }
                    ) {
                        ActionBottomSheet(
                            onAddTransaction = {
                                showActionSheet = false
                                navController.currentBackStackEntry?.savedStateHandle?.remove<Uri>("scannedImageUri")
                                navController.navigate(Screen.AddTransaction.route)
                            },
                            onScanTransaction = {
                                showActionSheet = false
                                // Navigate to transaction list with scanner enabled
                                navController.navigate(Screen.TransactionList.route)
                                // You might want to trigger scanner from here
                            },
                            onAddCredential = {
                                showActionSheet = false
                                navController.navigate(Screen.AddCredential.route)
                            },
                            onDismiss = { showActionSheet = false }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppPreview() {
    MainApp()
}
