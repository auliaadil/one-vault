package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.TransactionCategoryRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GetTransactionCategoriesCountUseCaseTest {

    @Mock
    private lateinit var transactionCategoryRepository: TransactionCategoryRepository

    private lateinit var getTransactionCategoriesCountUseCase: GetTransactionCategoriesCountUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getTransactionCategoriesCountUseCase = GetTransactionCategoriesCountUseCase(transactionCategoryRepository)
    }

    @Test
    fun `invoke returns correct count from repository`() = runTest {
        // Given
        val expectedCount = 8
        whenever(transactionCategoryRepository.getCategoriesCount()).thenReturn(expectedCount)

        // When
        val result = getTransactionCategoriesCountUseCase()

        // Then
        assertEquals(expectedCount, result)
    }

    @Test
    fun `invoke returns zero when no categories exist`() = runTest {
        // Given
        whenever(transactionCategoryRepository.getCategoriesCount()).thenReturn(0)

        // When
        val result = getTransactionCategoriesCountUseCase()

        // Then
        assertEquals(0, result)
    }
}
