package com.example.fffaaaa.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SectorEntity::class, TaskEntity::class], version = 1, exportSchema = true)
abstract class SDatabase : RoomDatabase() {
    abstract fun sectors(): SDao
    abstract fun tasks() : TDao

    companion object {
        private var INSTANCE: SDatabase? = null
        fun getAppDatabase(context: Context): SDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    SDatabase::class.java, "room-kotlin-database"
                ).build()
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}