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
fun AssignParticipantsScreen(
    splitBillViewModel: SplitBillViewModel = koinViewModel(),
    onProceedToSplitMethod: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val participants by splitBillViewModel.participants.collectAsState()
    val selectedParticipants by splitBillViewModel.selectedParticipants.collectAsState()

    var showAddParticipantDialog by remember { mutableStateOf(false) }

    GenericScreen(
        title = "Select Participants",
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = { showAddParticipantDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Participant")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (participants.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(R.dimen.spacing_large)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Groups,
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.icon_extra_large)),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))

                    Text(
                        text = "No Participants Yet",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Add people who will split this bill",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))

                    Button(
                        onClick = { showAddParticipantDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
                        Text("Add First Participant")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = dimensionResource(R.dimen.spacing_large)),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
                ) {
                    item {
                        Text(
                            text = "Select people to include in this split:",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = dimensionResource(R.dimen.spacing_medium))
                        )
                    }

                    items(participants) { participant ->
                        ParticipantSelectionCard(
                            participant = participant,
                            isSelected = selectedParticipants.any { it.id == participant.id },
                            onSelectionChange = { isSelected ->
                                val newSelection = if (isSelected) {
                                    selectedParticipants + participant
                                } else {
                                    selectedParticipants.filter { it.id != participant.id }
                                }
                                splitBillViewModel.updateSelectedParticipants(newSelection)
                            }
                        )
                    }
                }

                // Continue button
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.spacing_large))
                ) {
                    Column(
                        modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Selected Participants",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${selectedParticipants.size} people",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Icon(
                                Icons.Default.Groups,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_medium)))

                        Button(
                            onClick = onProceedToSplitMethod,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = selectedParticipants.isNotEmpty()
                        ) {
                            Text("Continue to Split Method")
                        }
                    }
                }
            }
        }
    }

    if (showAddParticipantDialog) {
        AddParticipantDialog(
            onDismiss = { showAddParticipantDialog = false },
            onAdd = { participant ->
                splitBillViewModel.addParticipant(participant)
                showAddParticipantDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantSelectionCard(
    participant: Participant,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = { onSelectionChange(!isSelected) }
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
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChange
            )

            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_medium)))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = participant.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                participant.email?.let { email ->
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                participant.phone?.let { phone ->
                    Text(
                        text = phone,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
fun AddParticipantDialog(
    onDismiss: () -> Unit,
    onAdd: (Participant) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Participant") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name *") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(Participant(
                            name = name.trim(),
                            email = email.trim().takeIf { it.isNotBlank() },
                            phone = phone.trim().takeIf { it.isNotBlank() }
                        ))
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AssignParticipantsScreenPreview() {
    val mockParticipants = listOf(
        Participant(id = 1, name = "John Doe", email = "john@example.com"),
        Participant(id = 2, name = "Jane Smith", phone = "+62 812 3456 7890"),
        Participant(id = 3, name = "Bob Wilson", email = "bob@example.com", phone = "+62 821 9876 5432")
    )

    OneVaultTheme {
        // Mock implementation for preview
        AssignParticipantsScreen()
    }
}
