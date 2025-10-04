package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Transaction
import com.adilstudio.project.onevault.domain.repository.TransactionRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class AddTransactionUseCaseTest {

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    private lateinit var addTransactionUseCase: AddTransactionUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        addTransactionUseCase = AddTransactionUseCase(transactionRepository)
    }

    @Test
    fun `invoke calls repository addTransaction with correct transaction`() = runTest {
        // Given
        val transaction = Transaction(
            id = 1L,
            title = "Electric Transaction",
            categoryId = 12L,
            amount = 150000.0,
            vendor = "PLN",
            transactionDate = "2024-01-15",
            imagePath = "/path/to/image.jpg",
            accountId = 123L
        )

        // When
        addTransactionUseCase(transaction)

        // Then
        verify(transactionRepository).addTransaction(transaction)
    }

    @Test
    fun `invoke handles transaction with minimal parameters`() = runTest {
        // Given
        val transaction = Transaction(
            id = 1L,
            title = "Electric Transaction",
            amount = 150000.0,
            vendor = "PLN",
            transactionDate = "2024-01-15"
        )

        // When
        addTransactionUseCase(transaction)

        // Then
        verify(transactionRepository).addTransaction(transaction)
    }
}
