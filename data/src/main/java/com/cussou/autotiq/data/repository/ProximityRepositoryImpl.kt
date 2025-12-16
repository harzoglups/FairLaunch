package com.cussou.autotiq.data.repository

import com.cussou.autotiq.data.local.dao.ProximityStateDao
import com.cussou.autotiq.data.mapper.toDomain
import com.cussou.autotiq.data.mapper.toEntity
import com.cussou.autotiq.domain.model.ProximityState
import com.cussou.autotiq.domain.repository.ProximityRepository
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
