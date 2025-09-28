package com.adilstudio.project.onevault.presentation.bill.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.model.BillCategory
import com.adilstudio.project.onevault.domain.model.CategoryType
import com.adilstudio.project.onevault.domain.usecase.AddBillCategoryUseCase
import com.adilstudio.project.onevault.domain.usecase.DeleteBillCategoryUseCase
import com.adilstudio.project.onevault.domain.usecase.GetBillCategoriesCountUseCase
import com.adilstudio.project.onevault.domain.usecase.GetBillCategoriesUseCase
import com.adilstudio.project.onevault.domain.usecase.InitializeDefaultBillCategoriesIfEmptyUseCase
import com.adilstudio.project.onevault.domain.usecase.UpdateBillCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillCategoryViewModel(
    private val getBillCategoriesUseCase: GetBillCategoriesUseCase,
    private val addBillCategoryUseCase: AddBillCategoryUseCase,
    private val updateBillCategoryUseCase: UpdateBillCategoryUseCase,
    private val deleteBillCategoryUseCase: DeleteBillCategoryUseCase,
    private val getBillCategoriesCountUseCase: GetBillCategoriesCountUseCase,
    private val initializeDefaultBillCategoriesIfEmptyUseCase: InitializeDefaultBillCategoriesIfEmptyUseCase
) : ViewModel() {

    private val _categories = MutableStateFlow<List<BillCategory>>(emptyList())
    val categories: StateFlow<List<BillCategory>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        viewModelScope.launch {
            initializeDefaultBillCategoriesIfEmptyUseCase()
            loadCategories()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getBillCategoriesUseCase().collect { categoryList ->
                    _categories.value = categoryList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    suspend fun checkAndInitializeDefaultCategories(defaultCategories: List<BillCategory>) {
        try {
            val categoriesCount = getBillCategoriesCountUseCase()
            if (categoriesCount == 0) {
                // No categories exist, initialize with defaults
                defaultCategories.forEach { category ->
                    addBillCategoryUseCase(category)
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
                val category = BillCategory(
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
                addBillCategoryUseCase(category)
                _successMessage.value = "Category '$name' added successfully"
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updateCategory(category: BillCategory) {
        viewModelScope.launch {
            try {
                updateBillCategoryUseCase(category.copy(updatedAt = System.currentTimeMillis()))
                _successMessage.value = "Category '${category.name}' updated successfully"
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteCategory(categoryId: Long) {
        viewModelScope.launch {
            try {
                deleteBillCategoryUseCase(categoryId)
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
