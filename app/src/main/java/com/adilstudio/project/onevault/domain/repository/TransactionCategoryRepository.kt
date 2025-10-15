package com.adilstudio.project.onevault.domain.repository

import com.adilstudio.project.onevault.domain.model.TransactionCategory
import com.adilstudio.project.onevault.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionCategoryRepository {
    fun getCategories(): Flow<List<TransactionCategory>>
    fun getCategoriesByTransactionType(transactionType: TransactionType): Flow<List<TransactionCategory>>
    suspend fun getCategoryById(id: Long): TransactionCategory?
    suspend fun addCategory(category: TransactionCategory)
    suspend fun updateCategory(category: TransactionCategory)
    suspend fun deleteCategory(id: Long)
    suspend fun getCategoriesCount(): Int
    suspend fun initializeDefaultCategoriesIfEmpty()
}
