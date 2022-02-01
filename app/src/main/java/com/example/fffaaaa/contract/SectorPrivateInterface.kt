package com.example.fffaaaa.contract

import androidx.annotation.Nullable
import com.example.fffaaaa.room.SectorEntity
import com.example.fffaaaa.room.TaskEntity

interface SectorPrivateInterface {
    fun changeSectorFromStartView(sectorEntity: SectorEntity, position: Int, taskEntity: TaskEntity, callsForAdding: Boolean)
}