package com.adilstudio.project.onevault.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.presentation.component.BaseScreen
import com.adilstudio.project.onevault.ui.theme.OneVaultTheme

data class HomeFeature(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToTransactions: () -> Unit = {},
    onNavigateToCategories: () -> Unit = {},
    onNavigateToAccounts: () -> Unit = {},
    onNavigateToCredentials: () -> Unit = {},
    onNavigateToInsights: () -> Unit = {},
    onNavigateToSplitBill: () -> Unit = {},
    onNavigateToBackupRestore: () -> Unit = {}
) {
    val features = listOf(
        HomeFeature(
            title = stringResource(R.string.transactions),
            icon = Icons.Filled.Receipt,
            onClick = onNavigateToTransactions
        ),
        HomeFeature(
            title = stringResource(R.string.categories),
            icon = Icons.Filled.Category,
            onClick = onNavigateToCategories
        ),
        HomeFeature(
            title = stringResource(R.string.accounts),
            icon = Icons.Filled.AccountBalance,
            onClick = onNavigateToAccounts
        ),
        HomeFeature(
            title = stringResource(R.string.credentials),
            icon = Icons.Filled.Lock,
            onClick = onNavigateToCredentials
        ),
//        HomeFeature(
//            title = "Insights",
//            icon = Icons.Filled.BarChart,
//            onClick = onNavigateToInsights
//        ),
        HomeFeature(
            title = stringResource(R.string.split_bill_title),
            icon = Icons.Filled.Group,
            onClick = onNavigateToSplitBill
        ),
//        HomeFeature(
//            title = "Backup Restore",
//            icon = Icons.Filled.Backup,
//            onClick = onNavigateToBackupRestore
//        )
    )

    BaseScreen(
        title = stringResource(R.string.home)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // SummaryCard placeholder for current month's total
//            SummaryCard()
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(features) { feature ->
                    FeatureCard(
                        feature = feature,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryCard() {
    // TODO: Bind to StatisticsViewModel for current month's total
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "Current Month Total",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Rp 0", // Placeholder, bind to ViewModel
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureCard(
    feature: HomeFeature,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = feature.onClick,
        modifier = modifier.aspectRatio(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = feature.icon,
                contentDescription = feature.title,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = feature.title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    OneVaultTheme {
        HomeScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun FeatureCardPreview() {
    OneVaultTheme {
        FeatureCard(
            feature = HomeFeature(
                title = "Transactions",
                icon = Icons.Filled.Receipt,
                onClick = {}
            )
        )
    }
}
