package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Account
import com.adilstudio.project.onevault.domain.repository.AccountRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class UpdateAccountUseCaseTest {

    @Mock
    private lateinit var accountRepository: AccountRepository

    private lateinit var updateAccountUseCase: UpdateAccountUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        updateAccountUseCase = UpdateAccountUseCase(accountRepository)
    }

    @Test
    fun `invoke calls repository updateAccount with correct account`() = runTest {
        // Given
        val account = Account(
            id = 123L,
            name = "Updated Account",
            amount = 1500.0,
            description = "Updated description"
        )

        // When
        updateAccountUseCase(account)

        // Then
        verify(accountRepository).updateAccount(account)
    }
}
