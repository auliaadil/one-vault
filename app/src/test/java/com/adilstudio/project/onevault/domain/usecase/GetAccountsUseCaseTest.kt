package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Account
import com.adilstudio.project.onevault.domain.repository.AccountRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GetAccountsUseCaseTest {

    @Mock
    private lateinit var accountRepository: AccountRepository

    private lateinit var getAccountsUseCase: GetAccountsUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getAccountsUseCase = GetAccountsUseCase(accountRepository)
    }

    @Test
    fun `invoke returns flow of accounts from repository`() = runTest {
        // Given
        val accounts = listOf(
            Account(id = "1", name = "Bank Account", amount = 1000.0),
            Account(id = "2", name = "Credit Card", amount = 500.0)
        )
        whenever(accountRepository.getAccounts()).thenReturn(flowOf(accounts))

        // When
        val result = getAccountsUseCase()

        // Then
        result.collect { accountList ->
            assertEquals(2, accountList.size)
            assertEquals("Bank Account", accountList[0].name)
            assertEquals("Credit Card", accountList[1].name)
        }
    }

    @Test
    fun `invoke returns empty list when no accounts exist`() = runTest {
        // Given
        whenever(accountRepository.getAccounts()).thenReturn(flowOf(emptyList()))

        // When
        val result = getAccountsUseCase()

        // Then
        result.collect { accountList ->
            assertTrue(accountList.isEmpty())
        }
    }
}
