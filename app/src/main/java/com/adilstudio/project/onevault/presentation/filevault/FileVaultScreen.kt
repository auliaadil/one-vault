package com.adilstudio.project.onevault.presentation.filevault

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.R
import org.koin.androidx.compose.koinViewModel

@Composable
fun FileVaultScreen(
    context: Context,
    viewModel: FileVaultViewModel = koinViewModel(),
    onPickFile: (onResult: (Uri) -> Unit) -> Unit,
    onImport: (onResult: (Uri) -> Unit) -> Unit,
    onExport: (onResult: (Uri) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val files = viewModel.files.collectAsState().value
    LaunchedEffect(Unit) { viewModel.loadFiles() }
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(R.string.file_vault), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(files.size) { idx ->
                val file = files[idx]
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(file.name, style = MaterialTheme.typography.titleMedium)
                        Text(file.filePath)
                    }
                }
            }
        }
        Button(onClick = {
            onPickFile { uri -> viewModel.addFile(context, uri) }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.add_file))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            onImport { uri -> viewModel.importData(context, uri) }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.import_data))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            onExport { uri -> viewModel.exportData(context, uri) }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.export_data))
        }
    }
}
