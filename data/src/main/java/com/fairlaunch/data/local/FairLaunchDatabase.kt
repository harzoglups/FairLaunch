package com.fairlaunch.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fairlaunch.data.local.dao.MapPointDao
import com.fairlaunch.data.local.dao.ProximityStateDao
import com.fairlaunch.data.local.entity.MapPointEntity
import com.fairlaunch.data.local.entity.ProximityStateEntity

@Database(
    entities = [MapPointEntity::class, ProximityStateEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FairLaunchDatabase : RoomDatabase() {
    abstract fun mapPointDao(): MapPointDao
    abstract fun proximityStateDao(): ProximityStateDao
}
