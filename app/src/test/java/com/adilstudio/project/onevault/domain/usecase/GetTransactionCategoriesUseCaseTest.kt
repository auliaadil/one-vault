package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.TransactionCategory
import com.adilstudio.project.onevault.domain.model.CategoryType
import com.adilstudio.project.onevault.domain.repository.TransactionCategoryRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GetTransactionCategoriesUseCaseTest {

    @Mock
    private lateinit var transactionCategoryRepository: TransactionCategoryRepository

    private lateinit var getTransactionCategoriesUseCase: GetTransactionCategoriesUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getTransactionCategoriesUseCase = GetTransactionCategoriesUseCase(transactionCategoryRepository)
    }

    @Test
    fun `invoke returns flow of categories from repository`() = runTest {
        // Given
        val categories = listOf(
            TransactionCategory(
                id = 1,
                name = "Utilities",
                icon = "⚡",
                color = "#FF5722",
                type = CategoryType.UTILITIES,
                createdAt = 1632835200000L,
                updatedAt = 1632835200000L
            ),
            TransactionCategory(
                id = 2,
                name = "Food",
                icon = "🍕",
                color = "#4CAF50",
                type = CategoryType.FOOD_AND_DINING,
                createdAt = 1632835200000L,
                updatedAt = 1632835200000L
            )
        )
        whenever(transactionCategoryRepository.getCategories()).thenReturn(flowOf(categories))

        // When
        val result = getTransactionCategoriesUseCase()

        // Then
        result.collect { categoryList ->
            assertEquals(2, categoryList.size)
            assertEquals("Utilities", categoryList[0].name)
            assertEquals("Food", categoryList[1].name)
        }
    }

    @Test
    fun `invoke returns empty list when no categories exist`() = runTest {
        // Given
        whenever(transactionCategoryRepository.getCategories()).thenReturn(flowOf(emptyList()))

        // When
        val result = getTransactionCategoriesUseCase()

        // Then
        result.collect { categoryList ->
            assertTrue(categoryList.isEmpty())
        }
    }
}
