package com.example.fffaaaa.room

import androidx.room.*
import java.time.LocalDateTime

@Dao
interface TDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(taskEntity: TaskEntity) : Long

    @Update
    fun update(taskEntity: TaskEntity)

    @Delete
    fun delete(taskEntity: TaskEntity)

    @Query("SELECT * FROM tasks WHERE sector_id =:id")
    fun getTaskListBySectorId(id: Long) : List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE sector_id =:id AND is_over=:isOver AND task_date > :localDateTime")
    fun getFreshTaskList(id: Long, isOver: Boolean, @TypeConverters(*[DateConverter::class]) localDateTime: LocalDateTime) : List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE id =:id")
    fun getTaskById(id: Long) : TaskEntity
}