package com.example.fffaaaa.room.daos

import androidx.room.*
import com.example.fffaaaa.room.enitites.SectorEntity

@Dao
interface SDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sector: SectorEntity)

    @Update
    fun update(sector: SectorEntity)

    @Delete
    fun delete(sector: SectorEntity)

    @Query("SELECT * FROM sectors WHERE id ==:id")
    fun findSectorById(id : Long) : SectorEntity

    @Query("SELECT * FROM sectors")
    fun getAllSectors() : List<SectorEntity>

    @Query("SELECT rem_count FROM sectors WHERE title =:title")
    fun getSectorTaskCount(title: String) : Int

    @Query("DELETE FROM sectors")
    fun clear()

    @Query("SELECT title FROM sectors ORDER BY CASE WHEN :isAsc = 1 THEN title END ASC, CASE WHEN :isAsc = 2 THEN title END DESC ")
    fun getSectorsSortedByName(isAsc : Int?): List<String>
}