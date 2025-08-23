package com.adilstudio.project.onevault.presentation.filevault

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
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
    Column(modifier = modifier.fillMaxSize().padding(dimensionResource(R.dimen.spacing_large))) {
        Text(stringResource(R.string.file_vault), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(files.size) { idx ->
                val file = files[idx]
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = dimensionResource(R.dimen.spacing_xs))) {
                    Column(modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large))) {
                        Text(file.name, style = MaterialTheme.typography.titleMedium)
                        Text(file.filePath)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
        Button(onClick = {
            onPickFile { uri -> viewModel.addFile(context, uri) }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.add_file))
        }
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
        Row {
            Button(onClick = {
                onImport { uri -> viewModel.importData(context, uri) }
            }, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.import_data))
            }
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
            Button(onClick = {
                onExport { uri -> viewModel.exportData(context, uri) }
            }, modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.export_data))
            }
        }
    }
}
