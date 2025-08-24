package com.adilstudio.project.onevault

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import com.adilstudio.project.onevault.presentation.navigation.NavGraph
import com.adilstudio.project.onevault.ui.theme.OneVaultTheme
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.adilstudio.project.onevault.presentation.navigation.Screen

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if launched from TileService
        val navigateTo = intent.getStringExtra("navigate_to")
        val initialRoute = when (navigateTo) {
            Screen.AddBill.route -> Screen.AddBill.route
            else -> null
        }

        setContent {
            MainApp(
                initialRoute = initialRoute
            )
        }
    }
}

@Composable
fun MainApp(
    initialRoute: String? = null
) {
    OneVaultTheme {
        val navController = rememberNavController()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                val items = listOf(
                    Triple(stringResource(R.string.bills), Screen.BillList.route, Icons.Filled.ShoppingCart),
                    Triple(stringResource(R.string.passwords), Screen.CredentialList.route, Icons.Filled.Lock),
                    Triple(stringResource(R.string.settings), Screen.Settings.route, Icons.Filled.Settings),
                )
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                NavigationBar {
                    items.forEach { (label, route, icon) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                            selected = currentRoute == route,
                            onClick = {
                                if (currentRoute != route) {
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
            NavGraph(navController = navController, modifier = Modifier.padding(innerPadding), startDestination = initialRoute ?: Screen.BillList.route)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppPreview() {
    MainApp()
}
