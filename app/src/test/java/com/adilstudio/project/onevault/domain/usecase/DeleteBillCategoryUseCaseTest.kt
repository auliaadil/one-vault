package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.BillCategoryRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class DeleteBillCategoryUseCaseTest {

    @Mock
    private lateinit var billCategoryRepository: BillCategoryRepository

    private lateinit var deleteBillCategoryUseCase: DeleteBillCategoryUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        deleteBillCategoryUseCase = DeleteBillCategoryUseCase(billCategoryRepository)
    }

    @Test
    fun `invoke calls repository deleteCategory with correct id`() = runTest {
        // Given
        val categoryId = "category-123"

        // When
        deleteBillCategoryUseCase(categoryId)

        // Then
        verify(billCategoryRepository).deleteCategory(categoryId)
    }
}
