package com.adilstudio.project.onevault.presentation.credential

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun CredentialListScreen(
    viewModel: PasswordManagerViewModel = koinViewModel(),
    onAddCredential: () -> Unit
) {
    val credentials = viewModel.credentials.collectAsState().value
    LaunchedEffect(Unit) { viewModel.loadCredentials() }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Credentials", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(credentials.size) { idx ->
                val credential = credentials[idx]
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(credential.serviceName, style = MaterialTheme.typography.titleMedium)
                        Text(credential.username)
                        // Do not display password
                    }
                }
            }
        }
        Button(onClick = onAddCredential, modifier = Modifier.fillMaxWidth()) {
            Text("Add Credential")
        }
    }
}
