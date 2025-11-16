package com.adilstudio.project.onevault.presentation.splitbill.form

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.domain.util.FeatureFlag
import com.adilstudio.project.onevault.presentation.MainViewModel
import com.adilstudio.project.onevault.presentation.component.BaseScreen
import com.adilstudio.project.onevault.presentation.splitbill.form.components.ImageCaptureStep
import com.adilstudio.project.onevault.presentation.splitbill.form.components.ItemAssignmentStep
import com.adilstudio.project.onevault.presentation.splitbill.form.components.OcrProcessingStep
import com.adilstudio.project.onevault.presentation.splitbill.form.components.ParticipantInputStep
import com.adilstudio.project.onevault.presentation.splitbill.form.components.SetupSplitBillStep
import com.adilstudio.project.onevault.presentation.splitbill.form.components.SplitBillSuccessBottomSheet
import com.adilstudio.project.onevault.presentation.splitbill.form.components.SummaryStep
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitBillFormScreen(
    viewModel: SplitBillFormViewModel = koinViewModel(),
    mainViewModel: MainViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
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
            mainViewModel.showSnackbar(message)
            viewModel.clearError()
        }
    }

    // Show success message as snackbar for split bill save
    uiState.saveSuccessMessage?.let { message ->
        LaunchedEffect(message) {
            mainViewModel.showSnackbar(message)
            viewModel.clearSuccessMessage()
        }
    }

    // Show success dialog when save is successful
    if (uiState.isSaveSuccessful) {
        // Calculate total amount for display
        val totalAmount = uiState.items.sumOf { item ->
            val totalQuantity = item.assignedQuantities.values.sum()
            item.price * totalQuantity
        } * (1 + (viewModel.tax / 100.0) + (viewModel.serviceFee / 100.0))

        SplitBillSuccessBottomSheet(
            title = viewModel.title,
            merchant = viewModel.merchant,
            date = viewModel.date,
            items = uiState.items,
            participants = uiState.calculatedParticipants,
            taxPercent = viewModel.tax,
            serviceFeePercent = viewModel.serviceFee,
            totalAmount = totalAmount,
            onDismiss = {
                viewModel.resetSaveSuccess()
                onNavigateBack()
            }
        )
    }

    BaseScreen(
        title = stringResource(R.string.split_bill_title),
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

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_large)))

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
                    label = stringResource(R.string.split_bill_title),
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
                                if (uiState.validationErrors.isEmpty()) {
                                    viewModel.saveSplitBill()
                                }
                            },
                            enabled = !uiState.isLoading && uiState.validationErrors.isEmpty()
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Saving...")
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
