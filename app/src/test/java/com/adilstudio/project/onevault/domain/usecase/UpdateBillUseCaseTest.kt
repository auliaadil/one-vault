package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Bill
import com.adilstudio.project.onevault.domain.repository.BillRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class UpdateBillUseCaseTest {

    @Mock
    private lateinit var billRepository: BillRepository

    private lateinit var updateBillUseCase: UpdateBillUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        updateBillUseCase = UpdateBillUseCase(billRepository)
    }

    @Test
    fun `invoke calls repository updateBill with correct bill`() = runTest {
        // Given
        val bill = Bill(
            id = 1L,
            title = "Updated Electric Bill",
            category = "Utilities",
            amount = 175000.0,
            vendor = "PLN",
            billDate = "2024-01-15",
            imagePath = "/path/to/updated_image.jpg",
            accountId = "account-123"
        )

        // When
        updateBillUseCase(bill)

        // Then
        verify(billRepository).updateBill(bill)
    }
}
