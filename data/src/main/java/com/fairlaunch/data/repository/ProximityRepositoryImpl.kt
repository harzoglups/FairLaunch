package com.fairlaunch.data.repository

import com.fairlaunch.data.local.dao.ProximityStateDao
import com.fairlaunch.data.mapper.toDomain
import com.fairlaunch.data.mapper.toEntity
import com.fairlaunch.domain.model.ProximityState
import com.fairlaunch.domain.repository.ProximityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProximityRepositoryImpl @Inject constructor(
    private val dao: ProximityStateDao
) : ProximityRepository {

    override fun getProximityState(pointId: Long): Flow<ProximityState?> {
        return dao.getProximityState(pointId).map { it?.toDomain() }
    }

    override suspend fun updateProximityState(state: ProximityState) {
        dao.insertProximityState(state.toEntity())
    }

    override suspend fun clearProximityState(pointId: Long) {
        dao.deleteProximityState(pointId)
    }
}
