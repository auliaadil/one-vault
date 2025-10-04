package com.adilstudio.project.onevault.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.adilstudio.project.onevault.Database
import com.adilstudio.project.onevault.domain.model.TransactionCategory
import com.adilstudio.project.onevault.domain.model.CategoryType
import com.adilstudio.project.onevault.domain.repository.TransactionCategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single

class TransactionCategoryRepositoryImpl(database: Database) : TransactionCategoryRepository {

    private val queries = database.transactionCategoryEntityQueries

    override fun getCategories(): Flow<List<TransactionCategory>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities ->
                entities.map { entity ->
                    TransactionCategory(
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

    override suspend fun getCategoryById(id: Long): TransactionCategory? {
        return queries.selectById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { entity ->
                entity?.let {
                    TransactionCategory(
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

    override suspend fun addCategory(category: TransactionCategory) {
        queries.insertCategory(
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

    override suspend fun updateCategory(category: TransactionCategory) {
        queries.updateCategory(
            name = category.name,
            icon = category.icon,
            color = category.color,
            type = category.type.name,
            parentCategoryId = category.parentCategoryId,
            isEditable = if (category.isEditable) 1L else 0L,
            updatedAt = category.updatedAt,
            id = category.id!!
        )
    }

    override suspend fun deleteCategory(id: Long) {
        queries.deleteCategory(id)
    }

    override suspend fun getCategoriesCount(): Int {
        return queries.selectAll().executeAsList().size
    }

    override suspend fun initializeDefaultCategoriesIfEmpty() {
        if (getCategoriesCount() == 0) {
            val defaultCategories = DefaultTransactionCategoriesHelper.createDefaultCategories()
            defaultCategories.forEach { addCategory(it) }
        }
    }
}
