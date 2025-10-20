package com.adilstudio.project.onevault.presentation.splitbill

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adilstudio.project.onevault.domain.util.FeatureFlag
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.presentation.component.BaseScreen
import com.adilstudio.project.onevault.presentation.splitbill.components.*
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import com.adilstudio.project.onevault.core.util.ShareImageGenerator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import com.adilstudio.project.onevault.core.util.RupiahFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitBillScreen(
    viewModel: SplitBillViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var isGeneratingImage by remember { mutableStateOf(false) }

    // Determine steps based on OCR feature flag
    val isOcrEnabled = FeatureFlag.isOcrSplitBillEnabled()
    val totalSteps = if (isOcrEnabled) 6 else 4

    // Map current step to progress index
    val currentStepIndex = when {
        isOcrEnabled -> uiState.currentStep.ordinal
        else -> when (uiState.currentStep) {
            SplitBillStep.OCR_REVIEW -> 0
            SplitBillStep.PARTICIPANT_INPUT -> 1
            SplitBillStep.ITEM_ASSIGNMENT -> 2
            SplitBillStep.SUMMARY -> 3
            else -> 0 // fallback, should not happen
        }
    }

    // Handle error messages
    uiState.errorMessage?.let { message ->
        LaunchedEffect(message) {
            // Show snack bar or toast
            viewModel.clearError()
        }
    }

    BaseScreen(
        title = "Split Bill",
        showNavIcon = true,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = { (currentStepIndex + 1) / totalSteps.toFloat() },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.secondary,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Step content with animation - Use weight to take available space
            Box(
                modifier = Modifier.weight(1f)
            ) {
                AnimatedContent(
                    targetState = uiState.currentStep,
                    transitionSpec = {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    },
                    label = "step_transition",
                    modifier = Modifier.fillMaxSize()
                ) { step ->
                    when (step) {
                        SplitBillStep.IMAGE_CAPTURE -> ImageCaptureStep(
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxSize()
                        )

                        SplitBillStep.OCR_PROCESSING -> OcrProcessingStep(
                            modifier = Modifier.fillMaxSize()
                        )

                        SplitBillStep.OCR_REVIEW -> SetupSplitBillStep(
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxSize()
                        )

                        SplitBillStep.PARTICIPANT_INPUT -> ParticipantInputStep(
                            viewModel = viewModel,
                            participants = uiState.participants,
                            modifier = Modifier.fillMaxSize()
                        )

                        SplitBillStep.ITEM_ASSIGNMENT -> ItemAssignmentStep(
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxSize()
                        )

                        SplitBillStep.SUMMARY -> SummaryStep(
                            viewModel = viewModel,
                            calculatedParticipants = uiState.calculatedParticipants,
                            splitBill = uiState.splitBill,
                            validationErrors = uiState.validationErrors,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // Navigation buttons - Fixed at bottom
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(R.dimen.spacing_xs))
                    .imePadding(),
                shape = MaterialTheme.shapes.medium,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (uiState.currentStep != SplitBillStep.IMAGE_CAPTURE) {
                        OutlinedButton(
                            onClick = { viewModel.previousStep() }
                        ) {
                            Text("Previous")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (uiState.currentStep != SplitBillStep.SUMMARY) {
                        Button(
                            onClick = { viewModel.nextStep() }
                        ) {
                            Text("Next")
                        }
                    } else {
                        Button(
                            onClick = {
                                if (!isGeneratingImage && uiState.validationErrors.isEmpty()) {
                                    isGeneratingImage = true

                                    // Calculate total amount
                                    val totalAmount = uiState.items.sumOf { item ->
                                        val totalQuantity = item.assignedQuantities.values.sum()
                                        item.price * totalQuantity
                                    } * (1 + (viewModel.tax / 100.0) + (viewModel.serviceFee / 100.0))

                                    // Generate image
                                    val shareImageGenerator = ShareImageGenerator(context)
                                    val imageUri = shareImageGenerator.generateCompleteSplitBillImage(
                                        billTitle = viewModel.title,
                                        merchant = viewModel.merchant,
                                        date = viewModel.date,
                                        items = uiState.items,
                                        participants = uiState.calculatedParticipants,
                                        taxPercent = viewModel.tax,
                                        serviceFeePercent = viewModel.serviceFee,
                                        totalAmount = totalAmount
                                    )

                                    if (imageUri != null) {
                                        val formattedTotal = RupiahFormatter.formatWithRupiahPrefix(totalAmount.toLong())
                                        val shareIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            type = "image/png"
                                            putExtra(Intent.EXTRA_STREAM, imageUri)
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "Split Bill: ${viewModel.title} - Total: $formattedTotal"
                                            )
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        context.startActivity(
                                            Intent.createChooser(
                                                shareIntent,
                                                "Share split bill"
                                            )
                                        )
                                    }

                                    isGeneratingImage = false
                                }
                            },
                            enabled = !isGeneratingImage && uiState.validationErrors.isEmpty()
                        ) {
                            if (isGeneratingImage) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generating...")
                            } else {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share Image")
                            }
                        }
                    }
                }
            }
        }
    }
}
