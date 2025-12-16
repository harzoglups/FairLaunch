package com.cussou.autotiq.domain.model

data class MapPoint(
    val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val name: String = "",
    val startHour: Int = 0, // 0-23, start hour for active window
    val startMinute: Int = 0, // 0-59, start minute for active window
    val endHour: Int = 23, // 0-23, end hour for active window
    val endMinute: Int = 59, // 0-59, end minute for active window
    val createdAt: Long = System.currentTimeMillis()
)
