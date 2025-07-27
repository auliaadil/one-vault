package com.adilstudio.project.onevault.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.adilstudio.project.onevault.Database
import com.adilstudio.project.onevault.domain.model.BillCategory
import com.adilstudio.project.onevault.domain.model.CategoryType
import com.adilstudio.project.onevault.domain.repository.BillCategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import java.util.UUID

class BillCategoryRepositoryImpl(private val database: Database) : BillCategoryRepository {

    private val queries = database.billCategoryEntityQueries

    override fun getCategories(): Flow<List<BillCategory>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    BillCategory(
                        id = entity.id,
                        name = entity.name,
                        icon = entity.icon,
                        color = entity.color,
                        type = CategoryType.valueOf(entity.type),
                        parentCategoryId = entity.parentCategoryId,
                        isEditable = entity.isEditable == 1L,
                        createdAt = entity.createdAt,
                        updatedAt = entity.updatedAt
                    )
                }
            }
    }

    override suspend fun getCategoryById(id: String): BillCategory? {
        return queries.selectById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity ->
                entity?.let {
                    BillCategory(
                        id = it.id,
                        name = it.name,
                        icon = it.icon,
                        color = it.color,
                        type = CategoryType.valueOf(it.type),
                        parentCategoryId = it.parentCategoryId,
                        isEditable = it.isEditable == 1L,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt
                    )
                }
            }.single()
    }

    override suspend fun addCategory(category: BillCategory) {
        queries.insertCategory(
            id = category.id,
            name = category.name,
            icon = category.icon,
            color = category.color,
            type = category.type.name,
            parentCategoryId = category.parentCategoryId,
            isEditable = if (category.isEditable) 1L else 0L,
            createdAt = category.createdAt,
            updatedAt = category.updatedAt
        )
    }

    override suspend fun updateCategory(category: BillCategory) {
        queries.updateCategory(
            name = category.name,
            icon = category.icon,
            color = category.color,
            type = category.type.name,
            parentCategoryId = category.parentCategoryId,
            isEditable = if (category.isEditable) 1L else 0L,
            updatedAt = System.currentTimeMillis(),
            id = category.id
        )
    }

    override suspend fun deleteCategory(id: String) {
        queries.deleteCategory(id)
    }

    override suspend fun initializeDefaultCategories() {
        // Check if categories already exist
        val existingCategories = queries.selectAll().executeAsList()
        if (existingCategories.isNotEmpty()) return

        val currentTime = System.currentTimeMillis()
        val defaultCategories = listOf(
            // ESSENTIAL Categories
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Electricity",
                icon = "⚡",
                color = "#FFB300",
                type = CategoryType.ESSENTIAL,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Water",
                icon = "💧",
                color = "#2196F3",
                type = CategoryType.ESSENTIAL,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Internet",
                icon = "🌐",
                color = "#4CAF50",
                type = CategoryType.ESSENTIAL,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Gas",
                icon = "🔥",
                color = "#FF5722",
                type = CategoryType.ESSENTIAL,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Groceries",
                icon = "🛒",
                color = "#8BC34A",
                type = CategoryType.ESSENTIAL,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Transportation",
                icon = "🚗",
                color = "#607D8B",
                type = CategoryType.ESSENTIAL,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Phone",
                icon = "📱",
                color = "#9C27B0",
                type = CategoryType.ESSENTIAL,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Healthcare",
                icon = "💊",
                color = "#F44336",
                type = CategoryType.ESSENTIAL,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Insurance",
                icon = "🛡️",
                color = "#3F51B5",
                type = CategoryType.ESSENTIAL,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Rent/Mortgage",
                icon = "🏠",
                color = "#795548",
                type = CategoryType.ESSENTIAL,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),

            // LIFESTYLE Categories
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Entertainment",
                icon = "🎬",
                color = "#E91E63",
                type = CategoryType.LIFESTYLE,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Dining Out",
                icon = "🍽️",
                color = "#FF9800",
                type = CategoryType.LIFESTYLE,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Shopping",
                icon = "🛍️",
                color = "#9C27B0",
                type = CategoryType.LIFESTYLE,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Coffee & Beverages",
                icon = "☕",
                color = "#8D6E63",
                type = CategoryType.LIFESTYLE,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Gym & Fitness",
                icon = "🏋️",
                color = "#FF5722",
                type = CategoryType.LIFESTYLE,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Subscriptions",
                icon = "📺",
                color = "#673AB7",
                type = CategoryType.LIFESTYLE,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Travel",
                icon = "✈️",
                color = "#00BCD4",
                type = CategoryType.LIFESTYLE,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Beauty & Personal Care",
                icon = "💄",
                color = "#E91E63",
                type = CategoryType.LIFESTYLE,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),

            // OTHER Categories
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Education",
                icon = "🎓",
                color = "#2196F3",
                type = CategoryType.OTHER,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Pet Care",
                icon = "🐕",
                color = "#4CAF50",
                type = CategoryType.OTHER,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Donations & Charity",
                icon = "❤️",
                color = "#F44336",
                type = CategoryType.OTHER,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Bank Fees",
                icon = "🏦",
                color = "#607D8B",
                type = CategoryType.OTHER,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Taxes",
                icon = "📊",
                color = "#795548",
                type = CategoryType.OTHER,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            ),
            BillCategory(
                id = UUID.randomUUID().toString(),
                name = "Miscellaneous",
                icon = "📋",
                color = "#9E9E9E",
                type = CategoryType.OTHER,
                isEditable = false,
                createdAt = currentTime,
                updatedAt = currentTime
            )
        )

        // Insert each category individually (like BillRepositoryImpl does)
        defaultCategories.forEach { category ->
            queries.insertCategory(
                id = category.id,
                name = category.name,
                icon = category.icon,
                color = category.color,
                type = category.type.name,
                parentCategoryId = category.parentCategoryId,
                isEditable = if (category.isEditable) 1L else 0L,
                createdAt = category.createdAt,
                updatedAt = category.updatedAt
            )
        }
    }
}
