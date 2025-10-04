package com.adilstudio.project.onevault.presentation.transaction.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.model.TransactionCategory
import com.adilstudio.project.onevault.domain.model.CategoryType
import com.adilstudio.project.onevault.domain.usecase.AddTransactionCategoryUseCase
import com.adilstudio.project.onevault.domain.usecase.DeleteTransactionCategoryUseCase
import com.adilstudio.project.onevault.domain.usecase.GetTransactionCategoriesCountUseCase
import com.adilstudio.project.onevault.domain.usecase.GetTransactionCategoriesUseCase
import com.adilstudio.project.onevault.domain.usecase.InitializeDefaultTransactionCategoriesIfEmptyUseCase
import com.adilstudio.project.onevault.domain.usecase.UpdateTransactionCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionCategoryViewModel(
    private val getTransactionCategoriesUseCase: GetTransactionCategoriesUseCase,
    private val addTransactionCategoryUseCase: AddTransactionCategoryUseCase,
    private val updateTransactionCategoryUseCase: UpdateTransactionCategoryUseCase,
    private val deleteTransactionCategoryUseCase: DeleteTransactionCategoryUseCase,
    private val getTransactionCategoriesCountUseCase: GetTransactionCategoriesCountUseCase,
    private val initializeDefaultTransactionCategoriesIfEmptyUseCase: InitializeDefaultTransactionCategoriesIfEmptyUseCase
) : ViewModel() {

    private val _categories = MutableStateFlow<List<TransactionCategory>>(emptyList())
    val categories: StateFlow<List<TransactionCategory>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        viewModelScope.launch {
            initializeDefaultTransactionCategoriesIfEmptyUseCase()
            loadCategories()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getTransactionCategoriesUseCase().collect { categoryList ->
                    _categories.value = categoryList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    suspend fun checkAndInitializeDefaultCategories(defaultCategories: List<TransactionCategory>) {
        try {
            val categoriesCount = getTransactionCategoriesCountUseCase()
            if (categoriesCount == 0) {
                // No categories exist, initialize with defaults
                defaultCategories.forEach { category ->
                    addTransactionCategoryUseCase(category)
                }
            }
        } catch (e: Exception) {
            _error.value = "Failed to initialize default categories: ${e.message}"
        }
    }

    fun addCategory(
        name: String,
        icon: String,
        color: String,
        type: CategoryType,
        parentCategoryId: Long? = null
    ) {
        viewModelScope.launch {
            try {
                val currentTime = System.currentTimeMillis()
                val category = TransactionCategory(
                    id = null,
                    name = name,
                    icon = icon,
                    color = color,
                    type = type,
                    parentCategoryId = parentCategoryId,
                    isEditable = true,
                    createdAt = currentTime,
                    updatedAt = currentTime
                )
                addTransactionCategoryUseCase(category)
                _successMessage.value = "Category '$name' added successfully"
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updateCategory(category: TransactionCategory) {
        viewModelScope.launch {
            try {
                updateTransactionCategoryUseCase(category.copy(updatedAt = System.currentTimeMillis()))
                _successMessage.value = "Category '${category.name}' updated successfully"
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            try {
                deleteTransactionCategoryUseCase(categoryId)
                _successMessage.value = "Category deleted successfully"
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}
