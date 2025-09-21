package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.BillCategoryRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GetBillCategoriesCountUseCaseTest {

    @Mock
    private lateinit var billCategoryRepository: BillCategoryRepository

    private lateinit var getBillCategoriesCountUseCase: GetBillCategoriesCountUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getBillCategoriesCountUseCase = GetBillCategoriesCountUseCase(billCategoryRepository)
    }

    @Test
    fun `invoke returns correct count from repository`() = runTest {
        // Given
        val expectedCount = 8
        whenever(billCategoryRepository.getCategoriesCount()).thenReturn(expectedCount)

        // When
        val result = getBillCategoriesCountUseCase()

        // Then
        assertEquals(expectedCount, result)
    }

    @Test
    fun `invoke returns zero when no categories exist`() = runTest {
        // Given
        whenever(billCategoryRepository.getCategoriesCount()).thenReturn(0)

        // When
        val result = getBillCategoriesCountUseCase()

        // Then
        assertEquals(0, result)
    }
}
