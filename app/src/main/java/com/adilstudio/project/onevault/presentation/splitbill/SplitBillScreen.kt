package com.adilstudio.project.onevault.presentation.splitbill

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adilstudio.project.onevault.domain.util.FeatureFlag
import com.adilstudio.project.onevault.presentation.component.BaseScreen
import com.adilstudio.project.onevault.presentation.splitbill.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitBillScreen(
    viewModel: SplitBillViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Add elevation to make it more prominent
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
                            onClick = { viewModel.saveSplitBill() },
                            enabled = !uiState.isLoading && uiState.validationErrors.isEmpty()
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Save Split Bill")
                            }
                        }
                    }
                }
            }
        }
    }
}
