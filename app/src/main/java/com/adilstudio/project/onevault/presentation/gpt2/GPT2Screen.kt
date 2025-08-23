package com.adilstudio.project.onevault.presentation.gpt2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.adilstudio.project.onevault.R
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GPT2Screen(
    onNavigateBack: () -> Unit,
    viewModel: GPT2ViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.ai_assistant))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        GPT2ScreenContent(
            uiState = uiState,
            onRefreshPrompt = viewModel::onRefreshPrompt,
            onGenerateText = viewModel::onGenerateText,
            onStopGeneration = viewModel::onStopGeneration,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GPT2ScreenContent(
    uiState: GPT2UiState,
    onRefreshPrompt: () -> Unit,
    onGenerateText: () -> Unit,
    onStopGeneration: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.gpt2_text_generation)) }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(dimensionResource(R.dimen.spacing_large)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_large))
        ) {
            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
            ) {
                OutlinedButton(
                    onClick = onRefreshPrompt,
                    enabled = !uiState.isGenerating && uiState.isInitialized
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
                    Text(stringResource(R.string.shuffle_prompt))
                }

                if (uiState.isGenerating) {
                    FilledTonalButton(
                        onClick = onStopGeneration
                    ) {
                        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_small)))
                        Text(stringResource(R.string.stop_generation))
                    }
                } else {
                    Button(
                        onClick = onGenerateText,
                        enabled = uiState.isInitialized
                    ) {
                        Text(stringResource(R.string.generate_text))
                    }
                }
            }

            // Status indicators
            when {
                !uiState.isInitialized -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(dimensionResource(R.dimen.icon_size_large)),
                                strokeWidth = dimensionResource(R.dimen.stroke_width_medium)
                            )
                            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_medium)))
                            Text(stringResource(R.string.initializing_gpt2_model))
                        }
                    }
                }

                uiState.isGenerating -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large)),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(dimensionResource(R.dimen.icon_size_large)),
                                strokeWidth = dimensionResource(R.dimen.stroke_width_medium)
                            )
                            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_medium)))
                            Text(stringResource(R.string.generating_text))
                        }
                    }
                }
            }

            // Error display
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large)),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Text display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_medium))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(dimensionResource(R.dimen.spacing_large))
                ) {
                    val fullText = buildAnnotatedString {
                        append(uiState.prompt)
                        if (uiState.completion.isNotEmpty()) {
                            withStyle(
                                style = SpanStyle(
                                    background = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    fontWeight = FontWeight.Medium
                                )
                            ) {
                                append(uiState.completion)
                            }
                        }
                    }

                    Text(
                        text = fullText,
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GPT2ScreenPreview() {
    MaterialTheme {
        GPT2ScreenContent(
            uiState = GPT2UiState(
                prompt = "Before boarding your rocket to Mars, remember to pack these items",
                completion = " like oxygen tanks, food supplies, and communication devices.",
                isGenerating = false,
                isInitialized = true,
                error = null
            ),
            onRefreshPrompt = {},
            onGenerateText = {},
            onStopGeneration = {}
        )
    }
}
