package com.adilstudio.project.onevault.data.repository

import com.adilstudio.project.onevault.data.local.PreferenceManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SettingsRepositoryImplTest {

    @Mock
    private lateinit var preferenceManager: PreferenceManager

    private lateinit var settingsRepository: SettingsRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        settingsRepository = SettingsRepositoryImpl(preferenceManager)
    }

    @Test
    fun `getBiometricEnabled returns correct value from preferences`() = runTest {
        // Given
        whenever(preferenceManager.getBiometricEnabledFlow()).thenReturn(flowOf(true))

        // When
        val result = settingsRepository.getBiometricEnabled().first()

        // Then
        assertTrue(result)
        verify(preferenceManager).getBiometricEnabledFlow()
    }

    @Test
    fun `setBiometricEnabled calls preference manager`() = runTest {
        // When
        settingsRepository.setBiometricEnabled(true)

        // Then
        verify(preferenceManager).setBiometricEnabled(true)
    }

    @Test
    fun `getAppLockTimeout returns correct value from preferences`() = runTest {
        // Given
        whenever(preferenceManager.getAppLockTimeoutFlow()).thenReturn(flowOf(60_000L))

        // When
        val result = settingsRepository.getAppLockTimeout().first()

        // Then
        assertEquals(60_000L, result)
    }

    @Test
    fun `setAppLockTimeout calls preference manager`() = runTest {
        // When
        settingsRepository.setAppLockTimeout(300_000L)

        // Then
        verify(preferenceManager).setAppLockTimeout(300_000L)
    }
}
