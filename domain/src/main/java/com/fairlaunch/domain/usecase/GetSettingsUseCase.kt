package com.fairlaunch.domain.usecase

import com.fairlaunch.domain.model.AppSettings
import com.fairlaunch.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetSettingsUseCase(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<AppSettings> {
        return repository.getSettings()
    }
}
