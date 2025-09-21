package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.BillRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class DeleteBillUseCaseTest {

    @Mock
    private lateinit var billRepository: BillRepository

    private lateinit var deleteBillUseCase: DeleteBillUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        deleteBillUseCase = DeleteBillUseCase(billRepository)
    }

    @Test
    fun `invoke calls repository deleteBill with correct id`() = runTest {
        // Given
        val billId = 123L

        // When
        deleteBillUseCase(billId)

        // Then
        verify(billRepository).deleteBill(billId)
    }
}
