package com.adilstudio.project.onevault.presentation.splitbill

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.domain.model.*
import com.adilstudio.project.onevault.presentation.component.GenericScreen
import com.adilstudio.project.onevault.ui.theme.OneVaultTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplitMethodScreen(
    splitBillViewModel: SplitBillViewModel = koinViewModel(),
    onProceedToResults: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val splitMethod by splitBillViewModel.splitMethod.collectAsState()
    val billItems by splitBillViewModel.billItems.collectAsState()
    val selectedParticipants by splitBillViewModel.selectedParticipants.collectAsState()
    val taxDistributionMethod by splitBillViewModel.taxDistributionMethod.collectAsState()
    val serviceDistributionMethod by splitBillViewModel.serviceDistributionMethod.collectAsState()

    LaunchedEffect(splitMethod, selectedParticipants, billItems) {
        splitBillViewModel.calculateSplit()
    }

    GenericScreen(
        title = "Split Method",
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = dimensionResource(R.dimen.spacing_large)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_medium))
        ) {
            item {
                Text(
                    text = "How would you like to split this bill?",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = dimensionResource(R.dimen.spacing_medium))
                )
            }

            item {
                SplitMethodCard(
                    method = SplitMethod.EQUAL,
                    title = "Equal Split",
                    description = "Divide the total amount evenly among all participants",
                    icon = Icons.Default.Balance,
                    isSelected = splitMethod == SplitMethod.EQUAL,
                    onSelect = { splitBillViewModel.updateSplitMethod(SplitMethod.EQUAL) }
                )
            }

            item {
                SplitMethodCard(
                    method = SplitMethod.BY_ITEM,
                    title = "By Item",
                    description = "Assign specific items to specific participants",
                    icon = Icons.Default.Assignment,
                    isSelected = splitMethod == SplitMethod.BY_ITEM,
                    onSelect = { splitBillViewModel.updateSplitMethod(SplitMethod.BY_ITEM) }
                )
            }

            item {
                SplitMethodCard(
                    method = SplitMethod.CUSTOM_RATIO,
                    title = "Custom Ratio",
                    description = "Set custom percentage or ratio for each participant",
                    icon = Icons.Default.Tune,
                    isSelected = splitMethod == SplitMethod.CUSTOM_RATIO,
                    onSelect = { splitBillViewModel.updateSplitMethod(SplitMethod.CUSTOM_RATIO) }
                )
            }

            if (splitMethod == SplitMethod.BY_ITEM) {
                item {
                    ItemAssignmentSection(
                        items = billItems,
                        participants = selectedParticipants,
                        onUpdateAssignments = { itemId, participantIds ->
                            splitBillViewModel.updateItemAssignments(itemId, participantIds)
                        }
                    )
                }
            }

            if (splitMethod == SplitMethod.CUSTOM_RATIO) {
                item {
                    CustomRatioSection(
                        participants = selectedParticipants,
                        onUpdateRatio = { participantId, ratio ->
                            val updatedParticipants = selectedParticipants.map { participant ->
                                if (participant.id == participantId) {
                                    participant.copy(ratio = ratio)
                                } else participant
                            }
                            splitBillViewModel.updateSelectedParticipants(updatedParticipants)
                        }
                    )
                }
            }

            item {
                TaxServiceDistributionCard(
                    taxDistributionMethod = taxDistributionMethod,
                    serviceDistributionMethod = serviceDistributionMethod,
                    onTaxMethodChange = { splitBillViewModel.updateTaxDistributionMethod(it) },
                    onServiceMethodChange = { splitBillViewModel.updateServiceDistributionMethod(it) }
                )
            }

            item {
                Button(
                    onClick = onProceedToResults,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Calculate & Review Split")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitMethodCard(
    method: SplitMethod,
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelect
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacing_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect
            )

            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_medium)))

            Icon(
                icon,
                contentDescription = null,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_medium)))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ItemAssignmentSection(
    items: List<BillItem>,
    participants: List<Participant>,
    onUpdateAssignments: (Long, List<Long>) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_medium))
        ) {
            Text(
                text = "Assign Items to Participants",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

            items.forEach { item ->
                ItemAssignmentRow(
                    item = item,
                    participants = participants,
                    onUpdateAssignments = { participantIds ->
                        onUpdateAssignments(item.id, participantIds)
                    }
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
            }
        }
    }
}

@Composable
fun ItemAssignmentRow(
    item: BillItem,
    participants: List<Participant>,
    onUpdateAssignments: (List<Long>) -> Unit
) {
    Column {
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        participants.forEach { participant ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = item.assignedParticipantIds.contains(participant.id),
                    onCheckedChange = { isChecked ->
                        val newAssignments = if (isChecked) {
                            item.assignedParticipantIds + participant.id
                        } else {
                            item.assignedParticipantIds - participant.id
                        }
                        onUpdateAssignments(newAssignments)
                    }
                )
                Text(
                    text = participant.name,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun CustomRatioSection(
    participants: List<Participant>,
    onUpdateRatio: (Long, Float) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_medium))
        ) {
            Text(
                text = "Set Custom Ratios",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Enter ratio for each participant (e.g., 1.0 = normal share, 0.5 = half share)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

            participants.forEach { participant ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = participant.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = participant.ratio.toString(),
                        onValueChange = { value ->
                            value.toFloatOrNull()?.let { ratio ->
                                onUpdateRatio(participant.id, ratio)
                            }
                        },
                        label = { Text("Ratio") },
                        modifier = Modifier.width(dimensionResource(R.dimen.text_field_small_width))
                    )
                }

                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
            }
        }
    }
}

@Composable
fun TaxServiceDistributionCard(
    taxDistributionMethod: TaxDistributionMethod,
    serviceDistributionMethod: TaxDistributionMethod,
    onTaxMethodChange: (TaxDistributionMethod) -> Unit,
    onServiceMethodChange: (TaxDistributionMethod) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_medium))
        ) {
            Text(
                text = "Tax & Service Distribution",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

            Text(
                text = "Tax Distribution:",
                style = MaterialTheme.typography.bodyMedium
            )

            Row {
                RadioButton(
                    selected = taxDistributionMethod == TaxDistributionMethod.PROPORTIONAL,
                    onClick = { onTaxMethodChange(TaxDistributionMethod.PROPORTIONAL) }
                )
                Text("Proportional", modifier = Modifier.padding(start = dimensionResource(R.dimen.spacing_small)))

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_medium)))

                RadioButton(
                    selected = taxDistributionMethod == TaxDistributionMethod.EVEN_SPLIT,
                    onClick = { onTaxMethodChange(TaxDistributionMethod.EVEN_SPLIT) }
                )
                Text("Even Split", modifier = Modifier.padding(start = dimensionResource(R.dimen.spacing_small)))
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

            Text(
                text = "Service Distribution:",
                style = MaterialTheme.typography.bodyMedium
            )

            Row {
                RadioButton(
                    selected = serviceDistributionMethod == TaxDistributionMethod.PROPORTIONAL,
                    onClick = { onServiceMethodChange(TaxDistributionMethod.PROPORTIONAL) }
                )
                Text("Proportional", modifier = Modifier.padding(start = dimensionResource(R.dimen.spacing_small)))

                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_medium)))

                RadioButton(
                    selected = serviceDistributionMethod == TaxDistributionMethod.EVEN_SPLIT,
                    onClick = { onServiceMethodChange(TaxDistributionMethod.EVEN_SPLIT) }
                )
                Text("Even Split", modifier = Modifier.padding(start = dimensionResource(R.dimen.spacing_small)))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplitMethodScreenPreview() {
    OneVaultTheme {
        SplitMethodScreen()
    }
}
