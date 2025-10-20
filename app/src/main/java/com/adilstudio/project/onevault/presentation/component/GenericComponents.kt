package com.adilstudio.project.onevault.presentation.component

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.core.util.ImageUtil
import com.adilstudio.project.onevault.presentation.MainViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Generic Screen wrapper that provides consistent layout structure
 * with optional topBar, floatingActionButton, and message handling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(
    title: String,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = koinViewModel(),
    showNavIcon: Boolean = false,
    actions: @Composable (RowScope.() -> Unit) = {},
    floatingActionButton: @Composable () -> Unit = {},
    successMessage: String? = null,
    errorMessage: String? = null,
    onClearSuccess: () -> Unit = {},
    onClearError: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    LaunchedEffect(Unit) {
        mainViewModel.updateTopBar(
            title = title,
            showNavigationIcon = showNavIcon,
            actions = actions
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

            // Content
            Box(modifier = Modifier.fillMaxSize()) {
                content(
                    PaddingValues(
                        horizontal = dimensionResource(R.dimen.spacing_large)
                    )
                )

                // Floating Action Button positioned at bottom right
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier.padding(dimensionResource(R.dimen.spacing_large))
                    ) {
                        floatingActionButton()
                    }
                }
            }
        }

        // Messages positioned at bottom like Snackbar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacing_large))
        ) {
            // Success message
            successMessage?.let { message ->
                MessageCard(
                    message = message,
                    isError = false,
                    onDismiss = onClearSuccess
                )
            }

            // Error message
            errorMessage?.let { message ->
                MessageCard(
                    message = message,
                    isError = true,
                    onDismiss = onClearError
                )
            }
        }
    }
}

/**
 * Generic message card for success and error messages
 */
@Composable
fun MessageCard(
    message: String,
    isError: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = dimensionResource(R.dimen.spacing_small)),
        colors = CardDefaults.cardColors(
            containerColor = if (isError)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacing_large)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message,
                color = if (isError)
                    MaterialTheme.colorScheme.onErrorContainer
                else
                    MaterialTheme.colorScheme.onPrimaryContainer,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            TextButton(
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.dismiss))
            }
        }
    }
}

/**
 * Generic empty state component
 */
@Composable
fun EmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Generic bottom sheet component with customizable header, edit/delete actions, and content slot
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericBottomSheet(
    title: String,
    modifier: Modifier = Modifier,
    onDelete: (() -> Unit)? = null,
    deleteDialogText: String? = null,
    onDeleteConfirmed: (() -> Unit)? = null,
    onDismiss: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.spacing_large)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
                Row {
                    if (onDelete != null) {
                        IconButton(onClick = {
                            if (deleteDialogText != null) {
                                showDeleteDialog = true
                            } else {
                                onDelete()
                            }
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }
                }
            }
            // Content slot
            content(PaddingValues(dimensionResource(R.dimen.spacing_large)))
        }
    }

    // Delete AlertDialog
    if (showDeleteDialog && deleteDialogText != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete)) },
            text = { Text(deleteDialogText) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDeleteConfirmed?.invoke() ?: onDelete?.invoke()
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun DetailField(
    labelRes: Int,
    modifier: Modifier = Modifier,
    value: String? = null,
    imagePath: String? = null,
    fontWeight: FontWeight? = null,
    maxLines: Int = Int.MAX_VALUE,
    trailing: (@Composable () -> Unit)? = null
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xs))
        ) {
            Text(
                text = stringResource(labelRes),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (value != null) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = fontWeight ?: FontWeight.Normal,
                    maxLines = maxLines
                )
            }
            if (imagePath != null) {
                val imageToShow = ImageUtil.getImageFileUri(context, imagePath)
                imageToShow?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Transaction Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensionResource(R.dimen.height_fixed_medium))
                            .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_radius_small)))
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, "image/*")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(intent)
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        if (trailing != null) {
            trailing()
        }
    }
}
