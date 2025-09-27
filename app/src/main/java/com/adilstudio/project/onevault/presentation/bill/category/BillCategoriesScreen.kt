package com.adilstudio.project.onevault.presentation.bill.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.domain.model.BillCategory
import com.adilstudio.project.onevault.domain.model.CategoryType
import com.adilstudio.project.onevault.presentation.component.BackNavigationIcon
import com.adilstudio.project.onevault.presentation.component.GenericScreen
import org.koin.androidx.compose.koinViewModel

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillCategoriesScreen(
    viewModel: BillCategoryViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<BillCategory?>(null) }
    var categoryToDelete by remember { mutableStateOf<BillCategory?>(null) }

    // Handle error messages
    LaunchedEffect(error) {
        error?.let {
            viewModel.clearError()
        }
    }

    // Handle success messages
    LaunchedEffect(successMessage) {
        successMessage?.let {
            viewModel.clearSuccessMessage()
        }
    }

    GenericScreen(
        title = stringResource(R.string.bill_categories),
        navigationIcon = {
            BackNavigationIcon(onNavigateBack = onNavigateBack)
        },
        successMessage = successMessage,
        errorMessage = error,
        onClearSuccess = { viewModel.clearSuccessMessage() },
        onClearError = { viewModel.clearError() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_category))
            }
        },
        defaultPaddingHorizontal = R.dimen.spacing_none
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Categories grouped by type
            val groupedCategories = categories.groupBy { it.type }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal = dimensionResource(R.dimen.spacing_large)),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryType.entries.forEach { type ->
                    val categoriesOfType = groupedCategories[type] ?: emptyList()
                    if (categoriesOfType.isNotEmpty()) {
                        item {
                            Text(
                                text = getCategoryTypeDisplayName(type),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(categoriesOfType) { category ->
                            CategoryCard(
                                category = category,
                                onEdit = { editingCategory = it },
                                onDelete = { categoryToDelete = it }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add/Edit Category Dialog
    if (showAddDialog || editingCategory != null) {
        CategoryFormDialog(
            category = editingCategory,
            onDismiss = {
                showAddDialog = false
                editingCategory = null
            },
            onSave = { name, icon, color, type ->
                if (editingCategory != null) {
                    viewModel.updateCategory(
                        editingCategory!!.copy(
                            name = name,
                            icon = icon,
                            color = color,
                            type = type
                        )
                    )
                } else {
                    viewModel.addCategory(name, icon, color, type)
                }
                showAddDialog = false
                editingCategory = null
            }
        )
    }

    // Delete Confirmation Dialog
    categoryToDelete?.let { category ->
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text(stringResource(R.string.delete_category)) },
            text = { Text(stringResource(R.string.delete_category_message, category.name)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCategory(category.id)
                        categoryToDelete = null
                    }
                ) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryCard(
    category: BillCategory,
    onEdit: (BillCategory) -> Unit,
    onDelete: (BillCategory) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit(category) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with background color
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(android.graphics.Color.parseColor(category.color))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.icon,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Category name
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = getCategoryTypeDisplayName(category.type),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Action buttons
            Row {
                if (category.isEditable) {
                    IconButton(onClick = { onEdit(category) }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit)
                        )
                    }
                    IconButton(onClick = { onDelete(category) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getCategoryTypeDisplayName(type: CategoryType): String {
    return when (type) {
        CategoryType.UTILITIES -> stringResource(R.string.utilities)
        CategoryType.FOOD_AND_DINING -> stringResource(R.string.food_and_dining)
        CategoryType.SHOPPING -> stringResource(R.string.shopping)
        CategoryType.TRANSPORTATION -> stringResource(R.string.transportation)
        CategoryType.ENTERTAINMENT -> stringResource(R.string.entertainment)
        CategoryType.HEALTHCARE -> stringResource(R.string.healthcare)
        CategoryType.EDUCATION -> stringResource(R.string.education)
        CategoryType.OTHER -> stringResource(R.string.other)
    }
}
