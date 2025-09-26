package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Account
import com.adilstudio.project.onevault.domain.repository.AccountRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class AddAccountUseCaseTest {

    @Mock
    private lateinit var accountRepository: AccountRepository

    private lateinit var addAccountUseCase: AddAccountUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        addAccountUseCase = AddAccountUseCase(accountRepository)
    }

    @Test
    fun `invoke calls repository addAccount with correct account`() = runTest {
        // Given
        val account = Account(
            id = 123L,
            name = "Test Account",
            amount = 1000.0,
            description = "Test description"
        )

        // When
        addAccountUseCase(account)

        // Then
        verify(accountRepository).addAccount(account)
    }

    @Test
    fun `invoke handles account with null description`() = runTest {
        // Given
        val account = Account(
            id = 123L,
            name = "Test Account",
            amount = 1000.0,
            description = null
        )

        // When
        addAccountUseCase(account)

        // Then
        verify(accountRepository).addAccount(account)
    }

    @Test
    fun `invoke handles account with default values`() = runTest {
        // Given
        val account = Account(
            id = 123L,
            name = "Test Account"
        )

        // When
        addAccountUseCase(account)

        // Then
        verify(accountRepository).addAccount(account)
    }
}
