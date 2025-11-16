package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Currency
import com.adilstudio.project.onevault.domain.repository.SettingsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class SetCurrencyUseCaseTest {

    @Mock
    private lateinit var settingsRepository: SettingsRepository

    private lateinit var setCurrencyUseCase: SetCurrencyUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        setCurrencyUseCase = SetCurrencyUseCase(settingsRepository)
    }

    @Test
    fun `invoke sets IDR currency code in repository`() = runTest {
        // When
        setCurrencyUseCase(Currency.IDR)

        // Then
        verify(settingsRepository).setCurrency("IDR")
    }

    @Test
    fun `invoke sets USD currency code in repository`() = runTest {
        // When
        setCurrencyUseCase(Currency.USD)

        // Then
        verify(settingsRepository).setCurrency("USD")
    }

    @Test
    fun `invoke sets EUR currency code in repository`() = runTest {
        // When
        setCurrencyUseCase(Currency.EUR)

        // Then
        verify(settingsRepository).setCurrency("EUR")
    }

    @Test
    fun `invoke sets MYR currency code in repository`() = runTest {
        // When
        setCurrencyUseCase(Currency.MYR)

        // Then
        verify(settingsRepository).setCurrency("MYR")
    }

    @Test
    fun `invoke sets SGD currency code in repository`() = runTest {
        // When
        setCurrencyUseCase(Currency.SGD)

        // Then
        verify(settingsRepository).setCurrency("SGD")
    }
}

