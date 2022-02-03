package com.example.fffaaaa.contract

import com.example.fffaaaa.room.enitites.SectorEntity
import com.example.fffaaaa.room.enitites.TaskEntity

interface SectorPrivateInterface {
    fun changeSectorFromStartView(sectorEntity: SectorEntity, position: Int, taskEntity: TaskEntity, callsForAdding: Boolean)
}