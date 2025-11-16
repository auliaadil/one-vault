package com.adilstudio.project.onevault.domain.usecase

import com.adilstudio.project.onevault.domain.model.Currency
import com.adilstudio.project.onevault.domain.repository.SettingsRepository

/**
 * Use case for setting the user's preferred currency
 */
class SetCurrencyUseCase(
    private val settingsRepository: SettingsRepository
) {
    /**
     * Set the user's preferred currency
     * @param currency The currency to set
     */
    suspend operator fun invoke(currency: Currency) {
        settingsRepository.setCurrency(currency.code)
    }
}

