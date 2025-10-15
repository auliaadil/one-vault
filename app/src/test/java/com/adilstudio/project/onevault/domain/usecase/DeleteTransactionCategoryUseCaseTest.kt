package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.TransactionCategoryRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class DeleteTransactionCategoryUseCaseTest {

    @Mock
    private lateinit var transactionCategoryRepository: TransactionCategoryRepository

    private lateinit var deleteTransactionCategoryUseCase: DeleteTransactionCategoryUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        deleteTransactionCategoryUseCase = DeleteTransactionCategoryUseCase(transactionCategoryRepository)
    }

    @Test
    fun `invoke calls repository deleteCategory with correct id`() = runTest {
        // Given
        val categoryId = 123L

        // When
        deleteTransactionCategoryUseCase(categoryId)

        // Then
        verify(transactionCategoryRepository).deleteCategory(categoryId)
    }
}
