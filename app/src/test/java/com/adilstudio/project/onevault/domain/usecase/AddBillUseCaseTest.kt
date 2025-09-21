package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Bill
import com.adilstudio.project.onevault.domain.repository.BillRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class AddBillUseCaseTest {

    @Mock
    private lateinit var billRepository: BillRepository

    private lateinit var addBillUseCase: AddBillUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        addBillUseCase = AddBillUseCase(billRepository)
    }

    @Test
    fun `invoke calls repository addBill with correct bill`() = runTest {
        // Given
        val bill = Bill(
            id = 1L,
            title = "Electric Bill",
            category = "Utilities",
            amount = 150000.0,
            vendor = "PLN",
            billDate = "2024-01-15",
            imagePath = "/path/to/image.jpg",
            accountId = "account-123"
        )

        // When
        addBillUseCase(bill)

        // Then
        verify(billRepository).addBill(bill)
    }

    @Test
    fun `invoke handles bill with minimal parameters`() = runTest {
        // Given
        val bill = Bill(
            id = 1L,
            title = "Electric Bill",
            amount = 150000.0,
            vendor = "PLN",
            billDate = "2024-01-15"
        )

        // When
        addBillUseCase(bill)

        // Then
        verify(billRepository).addBill(bill)
    }
}
