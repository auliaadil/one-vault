package com.adilstudio.project.onevault.presentation.action

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.ui.theme.OneVaultTheme

data class ActionItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionBottomSheet(
    onAddTransaction: () -> Unit = {},
    onScanTransaction: () -> Unit = {},
    onAddCredential: () -> Unit = {},
    onAddSplitBill: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val actions = listOf(
        ActionItem(
            title = stringResource(R.string.add_transaction),
            icon = Icons.Filled.Add,
            onClick = {
                onAddTransaction()
                onDismiss()
            }
        ),
        ActionItem(
            title = stringResource(R.string.scan_transaction),
            icon = Icons.Filled.DocumentScanner,
            onClick = {
                onScanTransaction()
                onDismiss()
            }
        ),
        ActionItem(
            title = stringResource(R.string.add_credential),
            icon = Icons.Filled.VpnKey,
            onClick = {
                onAddCredential()
                onDismiss()
            }
        ),
        ActionItem(
            title = stringResource(R.string.add_split_bill),
            icon = Icons.Filled.Group,
            onClick = {
                onAddSplitBill()
                onDismiss()
            }
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.quick_actions),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(actions) { action ->
                ActionItemRow(action = action)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionItemRow(
    action: ActionItem,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = action.onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = action.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActionBottomSheetPreview() {
    OneVaultTheme {
        ActionBottomSheet()
    }
}

@Preview(showBackground = true)
@Composable
fun ActionItemRowPreview() {
    OneVaultTheme {
        ActionItemRow(
            action = ActionItem(
                title = "Add Transaction",
                icon = Icons.Filled.Add,
                onClick = {}
            )
        )
    }
}
