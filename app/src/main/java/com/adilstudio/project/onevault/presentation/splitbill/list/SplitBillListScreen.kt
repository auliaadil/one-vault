package com.adilstudio.project.onevault.presentation.splitbill.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.core.util.DateUtil
import com.adilstudio.project.onevault.domain.model.SplitBill
import com.adilstudio.project.onevault.presentation.component.EmptyState
import com.adilstudio.project.onevault.presentation.component.BaseScreen
import com.adilstudio.project.onevault.presentation.component.AutoCurrencyText
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitBillListScreen(
    viewModel: SplitBillListViewModel = koinViewModel(),
    onAddSplitBill: () -> Unit
) {
    val splitBills by viewModel.splitBills.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedSplitBill by remember { mutableStateOf<SplitBill?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadSplitBills()
    }

    // Handle success messages
    LaunchedEffect(successMessage) {
        successMessage?.let {
            viewModel.clearSuccessMessage()
        }
    }

    // Handle error messages
    LaunchedEffect(error) {
        error?.let {
            viewModel.clearError()
        }
    }

    BaseScreen(
        title = "Split Bills",
        successMessage = successMessage,
        errorMessage = error,
        onClearSuccess = { viewModel.clearSuccessMessage() },
        onClearError = { viewModel.clearError() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddSplitBill
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Split Bill"
                )
            }
        },
        showNavIcon = true
    ) { paddingValues ->
        if (splitBills.isEmpty()) {
            EmptyState(
                title = "No Split Bills",
                description = "Tap the + button to create your first split bill",
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
            ) {
                items(
                    items = splitBills,
                    key = { it.id }
                ) { splitBill ->
                    SplitBillCard(
                        splitBill = splitBill,
                        onClick = { selectedSplitBill = splitBill }
                    )
                }
            }
        }
    }

    // Show split bill detail bottom sheet
    selectedSplitBill?.let { splitBill ->
        SplitBillDetailBottomSheet(
            splitBill = splitBill,
            onDismiss = { selectedSplitBill = null },
            onDelete = { id ->
                viewModel.deleteSplitBill(id)
                selectedSplitBill = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitBillCard(
    splitBill: SplitBill,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.spacing_xs))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacing_large)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
        ) {
            // Icon
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(dimensionResource(R.dimen.spacing_xxl))
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs))
            ) {
                Text(
                    text = splitBill.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (!splitBill.merchant.isNullOrBlank()) {
                    Text(
                        text = splitBill.merchant,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AutoCurrencyText(
                        amount = splitBill.totalAmount,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val localDate = DateUtil.isoStringToLocalDate(splitBill.date)
                    val displayDate = localDate?.let { DateUtil.formatDateForDisplay(it) } ?: splitBill.date
                    Text(
                        text = displayDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Participants count
                if (splitBill.participants.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Text(
                            text = "${splitBill.participants.size} participant${if (splitBill.participants.size > 1) "s" else ""}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(
                                horizontal = dimensionResource(R.dimen.spacing_small),
                                vertical = dimensionResource(R.dimen.spacing_xxs)
                            )
                        )
                    }
                }
            }
        }
    }
}
