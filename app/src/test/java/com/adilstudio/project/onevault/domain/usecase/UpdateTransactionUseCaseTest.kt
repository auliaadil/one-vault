package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Transaction
import com.adilstudio.project.onevault.domain.repository.TransactionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class UpdateTransactionUseCaseTest {

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    private lateinit var updateTransactionUseCase: UpdateTransactionUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        updateTransactionUseCase = UpdateTransactionUseCase(transactionRepository)
    }

    @Test
    fun `invoke calls repository updateTransaction with correct transaction`() = runTest {
        // Given
        val transaction = Transaction(
            id = 1L,
            title = "Updated Electric Transaction",
            categoryId = 12,
            amount = 175000.0,
            vendor = "PLN",
            transactionDate = "2024-01-15",
            imagePath = "/path/to/updated_image.jpg",
            accountId = 123L
        )

        // When
        updateTransactionUseCase(transaction)

        // Then
        verify(transactionRepository).updateTransaction(transaction)
    }
}
