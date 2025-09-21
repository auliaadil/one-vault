package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Bill
import com.adilstudio.project.onevault.domain.repository.BillRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GetBillsUseCaseTest {

    @Mock
    private lateinit var billRepository: BillRepository

    private lateinit var getBillsUseCase: GetBillsUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getBillsUseCase = GetBillsUseCase(billRepository)
    }

    @Test
    fun `invoke returns flow of bills from repository`() = runTest {
        // Given
        val bills = listOf(
            Bill(
                id = 1L,
                title = "Electric Bill",
                amount = 150000.0,
                vendor = "PLN",
                billDate = "2024-01-15"
            ),
            Bill(
                id = 2L,
                title = "Water Bill",
                amount = 50000.0,
                vendor = "PDAM",
                billDate = "2024-01-10"
            )
        )
        whenever(billRepository.getBills()).thenReturn(flowOf(bills))

        // When
        val result = getBillsUseCase()

        // Then
        result.collect { billList ->
            assertEquals(2, billList.size)
            assertEquals("Electric Bill", billList[0].title)
            assertEquals("Water Bill", billList[1].title)
        }
    }

    @Test
    fun `invoke returns empty list when no bills exist`() = runTest {
        // Given
        whenever(billRepository.getBills()).thenReturn(flowOf(emptyList()))

        // When
        val result = getBillsUseCase()

        // Then
        result.collect { billList ->
            assertTrue(billList.isEmpty())
        }
    }
}
