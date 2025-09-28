package com.adilstudio.project.onevault.domain.repository

import com.adilstudio.project.onevault.domain.model.BillCategory
import kotlinx.coroutines.flow.Flow

interface BillCategoryRepository {
    fun getCategories(): Flow<List<BillCategory>>
    suspend fun getCategoryById(id: Long): BillCategory?
    suspend fun addCategory(category: BillCategory)
    suspend fun updateCategory(category: BillCategory)
    suspend fun deleteCategory(id: Long)
    suspend fun getCategoriesCount(): Int
    suspend fun initializeDefaultCategoriesIfEmpty()
}
