package com.adilstudio.project.onevault.presentation.splitbill.form.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.domain.model.SplitParticipant
import com.adilstudio.project.onevault.presentation.splitbill.form.SplitBillFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantInputStep(
    viewModel: SplitBillFormViewModel,
    participants: List<SplitParticipant>,
    modifier: Modifier = Modifier
) {
    var newParticipantName by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier.padding(vertical = dimensionResource(R.dimen.spacing_large)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_large))
    ) {
        item {
            Text(
                text = stringResource(R.string.add_participants_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card {
                Column(modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large))) {
                    OutlinedTextField(
                        value = newParticipantName,
                        onValueChange = { newParticipantName = it },
                        label = { Text(stringResource(R.string.participant_placeholder)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
                    Button(
                        onClick = {
                            viewModel.addParticipant(newParticipantName)
                            newParticipantName = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.add_button))
                    }
                }
            }
        }

        item {
            Text(
                text = "Participants (${participants.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (participants.isEmpty()) {
            item {
                Card {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No participants added yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(participants) { participant ->
                ParticipantCard(
                    participant = participant,
                    onRemove = { viewModel.removeParticipant(participant.name) }
                )
            }
        }
    }
}

@Composable
fun ParticipantCard(
    participant: SplitParticipant,
    onRemove: () -> Unit
) {
    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = participant.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove ${participant.name}"
                )
            }
        }
    }
}
