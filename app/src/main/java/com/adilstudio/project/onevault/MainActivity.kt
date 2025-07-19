package com.adilstudio.project.onevault

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.FileProvider
import com.adilstudio.project.onevault.presentation.navigation.NavGraph
import com.adilstudio.project.onevault.ui.theme.OneVaultTheme
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    private var cameraImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Setup activity result launchers
        val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && cameraImageUri != null) {
                val image = InputImage.fromFilePath(this, cameraImageUri!!)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val lines = visionText.textBlocks.flatMap { it.lines }.map { it.text }
                        // Process scan results here
                        val title = lines.getOrNull(0) ?: ""
                        val amount = lines.find { it.contains("$", true) || it.matches(".*\\d+\\.\\d{2}".toRegex()) } ?: ""
                        val vendor = lines.getOrNull(1) ?: ""
                        // TODO: Pass results to UI or handle them
                    }
            }
        }

        val openDocumentLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let { pickedUri ->
                // TODO: Handle picked file here
            }
        }

        val createDocumentLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            uri?.let { exportUri ->
                // TODO: Handle export here
            }
        }

        setContent {
            MainApp(
                onScanBill = {
                    // Create image file and launch camera
                    val imageFile = File(filesDir, "camera_image.jpg")
                    cameraImageUri = FileProvider.getUriForFile(
                        this,
                        "${packageName}.fileprovider",
                        imageFile
                    )
                    cameraImageUri?.let { uri ->
                        cameraLauncher.launch(uri)
                    }
                },
                onPickFile = { onResult ->
                    openDocumentLauncher.launch(arrayOf("*/*"))
                },
                onExportFile = { onResult ->
                    createDocumentLauncher.launch("export.json")
                }
            )
        }
    }
}

@Composable
fun MainApp(
    onScanBill: () -> Unit = {},
    onPickFile: ((Uri) -> Unit) -> Unit = {},
    onExportFile: ((Uri) -> Unit) -> Unit = {}
) {
    OneVaultTheme {
        val navController = rememberNavController()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                val items = listOf(
                    Triple("Bill Tracker", "bill_list", Icons.Filled.ShoppingCart),
                    Triple("Password Manager", "credential_list", Icons.Filled.Lock),
                    Triple("File Vault", "file_vault", Icons.Filled.Email)
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
            NavGraph(navController = navController, modifier = Modifier.padding(innerPadding))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainAppPreview() {
    MainApp()
}
