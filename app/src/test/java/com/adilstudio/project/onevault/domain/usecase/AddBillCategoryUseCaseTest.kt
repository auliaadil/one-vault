package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.BillCategory
import com.adilstudio.project.onevault.domain.model.CategoryType
import com.adilstudio.project.onevault.domain.repository.BillCategoryRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class AddBillCategoryUseCaseTest {

    @Mock
    private lateinit var billCategoryRepository: BillCategoryRepository

    private lateinit var addBillCategoryUseCase: AddBillCategoryUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        addBillCategoryUseCase = AddBillCategoryUseCase(billCategoryRepository)
    }

    @Test
    fun `invoke calls repository addCategory with correct category`() = runTest {
        // Given
        val category = BillCategory(
            id = "category-123",
            name = "Utilities",
            icon = "⚡",
            color = "#FF5722",
            type = CategoryType.UTILITIES,
            createdAt = 1632835200000L,
            updatedAt = 1632835200000L
        )

        // When
        addBillCategoryUseCase(category)

        // Then
        verify(billCategoryRepository).addCategory(category)
    }
}
