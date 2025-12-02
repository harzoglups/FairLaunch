package com.fairlaunch.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fairlaunch.data.local.entity.ProximityStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProximityStateDao {
    @Query("SELECT * FROM proximity_states WHERE pointId = :pointId")
    fun getProximityState(pointId: Long): Flow<ProximityStateEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProximityState(state: ProximityStateEntity)
    
    @Query("DELETE FROM proximity_states WHERE pointId = :pointId")
    suspend fun deleteProximityState(pointId: Long)
}
