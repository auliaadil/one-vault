package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.repository.AccountRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class DeleteAccountUseCaseTest {

    @Mock
    private lateinit var accountRepository: AccountRepository

    private lateinit var deleteAccountUseCase: DeleteAccountUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        deleteAccountUseCase = DeleteAccountUseCase(accountRepository)
    }

    @Test
    fun `invoke calls repository deleteAccount with correct id`() = runTest {
        // Given
        val accountId = 123L

        // When
        deleteAccountUseCase(accountId)

        // Then
        verify(accountRepository).deleteAccount(accountId)
    }
}
