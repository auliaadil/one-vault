package com.adilstudio.project.onevault.data.repository

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

class BillCategoryRepositoryImpl(database: Database) : BillCategoryRepository {

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

    override suspend fun getCategoriesCount(): Int {
        return queries.selectAll().executeAsList().size
    }
}
