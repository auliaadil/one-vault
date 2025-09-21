package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.AccountRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GetAccountsCountUseCaseTest {

    @Mock
    private lateinit var accountRepository: AccountRepository

    private lateinit var getAccountsCountUseCase: GetAccountsCountUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getAccountsCountUseCase = GetAccountsCountUseCase(accountRepository)
    }

    @Test
    fun `invoke returns correct count from repository`() = runTest {
        // Given
        val expectedCount = 5
        whenever(accountRepository.getAccountsCount()).thenReturn(expectedCount)

        // When
        val result = getAccountsCountUseCase()

        // Then
        assertEquals(expectedCount, result)
    }

    @Test
    fun `invoke returns zero when no accounts exist`() = runTest {
        // Given
        whenever(accountRepository.getAccountsCount()).thenReturn(0)

        // When
        val result = getAccountsCountUseCase()

        // Then
        assertEquals(0, result)
    }
}
