package com.adilstudio.project.onevault.presentation.component

import androidx.annotation.DimenRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.adilstudio.project.onevault.R
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.SheetValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.adilstudio.project.onevault.core.util.ImageUtil

/**
 * Generic Screen wrapper that provides consistent layout structure
 * with optional topBar, floatingActionButton, and message handling
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericScreen(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    floatingActionButton: @Composable () -> Unit = {},
    successMessage: String? = null,
    errorMessage: String? = null,
    onClearSuccess: () -> Unit = {},
    onClearError: () -> Unit = {},
    @DimenRes defaultPaddingHorizontal: Int = R.dimen.spacing_large,
    content: @Composable (PaddingValues) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            TopAppBar(
                title = { Text(text = title) },
                navigationIcon = navigationIcon,
                actions = actions
            )

            // Content area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensionResource(defaultPaddingHorizontal))
            ) {
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_small)))

                // Content
                Box(modifier = Modifier.fillMaxSize()) {
                    content(PaddingValues())

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
 * Generic back navigation icon
 */
@Composable
fun BackNavigationIcon(
    onNavigateBack: () -> Unit
) {
    IconButton(onClick = onNavigateBack) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.back)
        )
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
    onEdit: (() -> Unit)? = null,
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
                    if (onEdit != null) {
                        IconButton(onClick = {
                            onEdit()
                        }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit)
                            )
                        }
                    }
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
    value: String? = null,
    imagePath: String? = null,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight? = null,
    maxLines: Int = Int.MAX_VALUE,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = modifier.fillMaxWidth().weight(1f),
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
                // Show existing image
                val imageToShow = ImageUtil.getImageFileUri(LocalContext.current, imagePath)
                imageToShow?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = "Bill Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensionResource(R.dimen.height_fixed_medium))
                            .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_radius_small))),
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
