package com.adilstudio.project.onevault.presentation.bill.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.adilstudio.project.onevault.R
import com.adilstudio.project.onevault.domain.model.BillCategory
import com.adilstudio.project.onevault.domain.model.CategoryType
import androidx.core.graphics.toColorInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFormDialog(
    category: BillCategory? = null,
    onDismiss: () -> Unit,
    onSave: (name: String, icon: String, color: String, type: CategoryType) -> Unit
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var selectedIcon by remember { mutableStateOf(category?.icon ?: "📋") }
    var selectedColor by remember { mutableStateOf(category?.color ?: "#2196F3") }
    var selectedType by remember { mutableStateOf(category?.type ?: CategoryType.OTHERS) }

    val isEditing = category != null
    val title = if (isEditing) stringResource(R.string.edit_category) else stringResource(R.string.add_category)

    // Predefined icons for selection
    val availableIcons = listOf(
        "⚡", "💧", "🌐", "🔥", "🛒", "🚗", "🎬", "🍽️", "🛍️", "📋",
        "🏠", "📱", "💊", "🎓", "💰", "🎵", "🏋️", "✈️", "🐕", "🌟",
        "📚", "🎨", "☕", "🍕", "🚀", "💻", "📺", "🎮", "🧘", "🏆"
    )

    // Predefined colors for selection
    val availableColors = listOf(
        "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3",
        "#03A9F4", "#00BCD4", "#009688", "#4CAF50", "#8BC34A", "#CDDC39",
        "#FFEB3B", "#FFC107", "#FF9800", "#FF5722", "#795548", "#607D8B"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name input
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.category_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    ),
                    singleLine = true
                )

                // Icon selection
                Column {
                    Text(stringResource(R.string.select_icon), style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(availableIcons) { icon ->
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (selectedIcon == icon)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { selectedIcon = icon },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = icon,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }

                // Color selection
                Column {
                    Text(stringResource(R.string.select_color), style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(availableColors) { color ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(color.toColorInt()))
                                    .clickable { selectedColor = color }
                            ) {
                                if (selectedColor == color) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(Color.Black.copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "✓",
                                            color = Color.White,
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Type selection
                Column {
                    Text(stringResource(R.string.category_type), style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    CategoryType.entries.forEach { type ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedType = type },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedType == type,
                                onClick = { selectedType = type }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = getCategoryTypeDisplayName(type),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Preview
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(selectedColor.toColorInt())),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = selectedIcon,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = name.ifEmpty { stringResource(R.string.category_name) },
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = getCategoryTypeDisplayName(selectedType),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name.trim(), selectedIcon, selectedColor, selectedType)
                    }
                },
                enabled = name.isNotBlank()
            ) {
                Text(if (isEditing) stringResource(R.string.update) else stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
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
        CategoryType.OTHERS -> stringResource(R.string.other)
    }
}
