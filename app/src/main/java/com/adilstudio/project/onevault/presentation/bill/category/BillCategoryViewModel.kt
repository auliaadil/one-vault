package com.adilstudio.project.onevault.presentation.bill.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adilstudio.project.onevault.domain.model.BillCategory
import com.adilstudio.project.onevault.domain.model.CategoryType
import com.adilstudio.project.onevault.domain.usecase.AddBillCategoryUseCase
import com.adilstudio.project.onevault.domain.usecase.DeleteBillCategoryUseCase
import com.adilstudio.project.onevault.domain.usecase.GetBillCategoriesUseCase
import com.adilstudio.project.onevault.domain.usecase.UpdateBillCategoryUseCase
import com.adilstudio.project.onevault.domain.usecase.InitializeDefaultCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class BillCategoryViewModel(
    private val getBillCategoriesUseCase: GetBillCategoriesUseCase,
    private val addBillCategoryUseCase: AddBillCategoryUseCase,
    private val updateBillCategoryUseCase: UpdateBillCategoryUseCase,
    private val deleteBillCategoryUseCase: DeleteBillCategoryUseCase,
    private val initializeDefaultCategoriesUseCase: InitializeDefaultCategoriesUseCase
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
        initializeData()
    }

    private fun initializeData() {
        viewModelScope.launch {
            try {
                // Initialize default categories if they don't exist
                initializeDefaultCategoriesUseCase()
                // Then load categories
                loadCategories()
            } catch (e: Exception) {
                _error.value = "Failed to initialize categories: ${e.message}"
                // Still try to load existing categories
                loadCategories()
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getBillCategoriesUseCase().collect { categoryList ->
                    _categories.value = categoryList
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addCategory(
        name: String,
        icon: String,
        color: String,
        type: CategoryType,
        parentCategoryId: String? = null
    ) {
        viewModelScope.launch {
            try {
                val currentTime = System.currentTimeMillis()
                val category = BillCategory(
                    id = UUID.randomUUID().toString(),
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
                // Refresh categories list
                loadCategories()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun updateCategory(category: BillCategory) {
        viewModelScope.launch {
            try {
                val updatedCategory = category.copy(updatedAt = System.currentTimeMillis())
                updateBillCategoryUseCase(updatedCategory)
                _successMessage.value = "Category '${category.name}' updated successfully"
                // Refresh categories list
                loadCategories()
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            try {
                // Get category name before deletion for success message
                val categoryName = _categories.value.find { it.id == id }?.name ?: "Category"
                deleteBillCategoryUseCase(id)
                _successMessage.value = "Category '$categoryName' deleted successfully"
                // Refresh categories list
                loadCategories()
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
