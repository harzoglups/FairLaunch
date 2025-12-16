package com.cussou.autotiq.data.repository

import com.cussou.autotiq.data.local.dao.MapPointDao
import com.cussou.autotiq.data.mapper.toDomain
import com.cussou.autotiq.data.mapper.toEntity
import com.cussou.autotiq.domain.model.MapPoint
import com.cussou.autotiq.domain.repository.MapPointRepository
import com.cussou.autotiq.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MapPointRepositoryImpl @Inject constructor(
    private val dao: MapPointDao
) : MapPointRepository {

    override fun getAllPoints(): Flow<List<MapPoint>> {
        return dao.getAllPoints().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getPointById(id: Long): Result<MapPoint> {
        return try {
            val entity = dao.getPointById(id)
            if (entity != null) {
                Result.Success(entity.toDomain())
            } else {
                Result.Error(Exception("Point not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun insertPoint(point: MapPoint): Result<Long> {
        return try {
            val id = dao.insertPoint(point.toEntity())
            Result.Success(id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deletePoint(id: Long): Result<Unit> {
        return try {
            dao.deletePoint(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updatePoint(point: MapPoint): Result<Unit> {
        return try {
            dao.updatePoint(point.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
