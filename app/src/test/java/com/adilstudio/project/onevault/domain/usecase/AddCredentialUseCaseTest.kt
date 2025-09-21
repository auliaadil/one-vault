package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Credential
import com.adilstudio.project.onevault.domain.repository.CredentialRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class AddCredentialUseCaseTest {

    @Mock
    private lateinit var credentialRepository: CredentialRepository

    private lateinit var addCredentialUseCase: AddCredentialUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        addCredentialUseCase = AddCredentialUseCase(credentialRepository)
    }

    @Test
    fun `invoke calls repository addCredential with correct credential`() = runTest {
        // Given
        val credential = Credential(
            id = 1L,
            serviceName = "Google",
            username = "user@example.com",
            password = "securePassword123",
        )

        // When
        addCredentialUseCase(credential)

        // Then
        verify(credentialRepository).addCredential(credential)
    }

    @Test
    fun `invoke handles template-based credential`() = runTest {
        // Given
        val credential = Credential(
            id = 1L,
            serviceName = "GitHub",
            username = "developer",
            password = "generated123",
        )

        // When
        addCredentialUseCase(credential)

        // Then
        verify(credentialRepository).addCredential(credential)
    }
}
