package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Currency
import com.adilstudio.project.onevault.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case for getting the user's selected currency
 */
class GetCurrencyUseCase(
    private val settingsRepository: SettingsRepository
) {
    /**
     * Get the current currency as a Flow
     * @return Flow of Currency enum
     */
    operator fun invoke(): Flow<Currency> {
        return settingsRepository.getCurrency().map { currencyCode ->
            Currency.fromCode(currencyCode)
        }
    }
}

