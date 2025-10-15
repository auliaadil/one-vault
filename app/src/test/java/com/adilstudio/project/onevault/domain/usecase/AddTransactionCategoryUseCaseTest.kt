package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.TransactionCategory
import com.adilstudio.project.onevault.domain.model.CategoryType
import com.adilstudio.project.onevault.domain.repository.TransactionCategoryRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class AddTransactionCategoryUseCaseTest {

    @Mock
    private lateinit var transactionCategoryRepository: TransactionCategoryRepository

    private lateinit var addTransactionCategoryUseCase: AddTransactionCategoryUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        addTransactionCategoryUseCase = AddTransactionCategoryUseCase(transactionCategoryRepository)
    }

    @Test
    fun `invoke calls repository addCategory with correct category`() = runTest {
        // Given
        val category = TransactionCategory(
            id = 123,
            name = "Utilities",
            icon = "⚡",
            color = "#FF5722",
            type = CategoryType.UTILITIES,
            createdAt = 1632835200000L,
            updatedAt = 1632835200000L
        )

        // When
        addTransactionCategoryUseCase(category)

        // Then
        verify(transactionCategoryRepository).addCategory(category)
    }
}
