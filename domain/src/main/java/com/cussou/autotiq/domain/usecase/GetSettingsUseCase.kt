package com.cussou.autotiq.domain.usecase

import com.cussou.autotiq.domain.model.AppSettings
import com.cussou.autotiq.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetSettingsUseCase(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<AppSettings> {
        return repository.getSettings()
    }
}
