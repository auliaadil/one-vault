package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Transaction
import com.adilstudio.project.onevault.domain.model.TransactionType
import com.adilstudio.project.onevault.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GetTransactionsUseCaseTest {

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    private lateinit var getTransactionsUseCase: GetTransactionsUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getTransactionsUseCase = GetTransactionsUseCase(transactionRepository)
    }

    @Test
    fun `invoke returns flow of transactions from repository`() = runTest {
        // Given
        val transactions = listOf(
            Transaction(
                id = 1L,
                title = "Electric Transaction",
                amount = 150000.0,
                merchant = "PLN", // Updated from vendor
                date = "2024-01-15", // Updated from transactionDate
                type = TransactionType.EXPENSE // New field
            ),
            Transaction(
                id = 2L,
                title = "Water Transaction",
                amount = 50000.0,
                merchant = "PDAM", // Updated from vendor
                date = "2024-01-10", // Updated from transactionDate
                type = TransactionType.EXPENSE // New field
            )
        )
        whenever(transactionRepository.getTransactions()).thenReturn(flowOf(transactions))

        // When
        val result = getTransactionsUseCase()

        // Then
        result.collect { transactionList ->
            assertEquals(2, transactionList.size)
            assertEquals("Electric Transaction", transactionList[0].title)
            assertEquals("Water Transaction", transactionList[1].title)
        }
    }

    @Test
    fun `invoke returns empty list when no transactions exist`() = runTest {
        // Given
        whenever(transactionRepository.getTransactions()).thenReturn(flowOf(emptyList()))

        // When
        val result = getTransactionsUseCase()

        // Then
        result.collect { transactionList ->
            assertTrue(transactionList.isEmpty())
        }
    }
}
