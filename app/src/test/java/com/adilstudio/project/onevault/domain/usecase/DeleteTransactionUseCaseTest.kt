package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.TransactionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class DeleteTransactionUseCaseTest {

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    private lateinit var deleteTransactionUseCase: DeleteTransactionUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        deleteTransactionUseCase = DeleteTransactionUseCase(transactionRepository)
    }

    @Test
    fun `invoke calls repository deleteTransaction with correct id`() = runTest {
        // Given
        val transactionId = 123L

        // When
        deleteTransactionUseCase(transactionId)

        // Then
        verify(transactionRepository).deleteTransaction(transactionId)
    }
}
