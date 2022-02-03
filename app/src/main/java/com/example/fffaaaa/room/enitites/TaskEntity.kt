package com.example.fffaaaa.room.enitites

import androidx.room.*
import com.example.fffaaaa.room.converter.DateConverter
import com.example.fffaaaa.room.daos.TDao
import kotlinx.coroutines.*
import java.time.LocalDateTime

@Entity(tableName = "tasks")
@TypeConverters(value = [DateConverter::class])
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    var id : Long,
    @ColumnInfo(name = "sector_id")
    var sectorId: Long,
    @ColumnInfo(name = "task_title")
    var taskTitle: String,
    @ColumnInfo(name = "task_description")
    var taskDescription: String,
    @ColumnInfo(name = "task_date")
    var taskDate: LocalDateTime,
    @ColumnInfo(name = "is_over")
    var isOver: Boolean
) {
    companion object {
            @DelicateCoroutinesApi
            fun insert(dao: TDao, taskEntity : TaskEntity) : Long{
                var id: Long = 0
                runBlocking {
                    val job = GlobalScope.launch(Dispatchers.IO) {
                        id = dao.insert(taskEntity)
                    }
                    job.join()
                }
                return id
            }

            @DelicateCoroutinesApi
            fun update(dao: TDao, taskEntity: TaskEntity) {
                runBlocking {
                    val job = GlobalScope.launch(Dispatchers.IO) {
                        dao.update(taskEntity)
                    }
                    job.join()
                }
            }

            @DelicateCoroutinesApi
            fun delete(dao: TDao, taskEntity: TaskEntity) {
                runBlocking {
                    val job = GlobalScope.launch(Dispatchers.IO) {
                        dao.delete(taskEntity)
                    }
                    job.join()
                }
            }
            @DelicateCoroutinesApi
            fun delete(dao: TDao, taskEntityList: List<TaskEntity>) {
                runBlocking {
                    val job = GlobalScope.launch(Dispatchers.IO) {
                        dao.delete(taskEntityList)
                    }
                    job.join()
                }
            }
        @DelicateCoroutinesApi
        fun getTaskListBySectorId(id: Long, dao: TDao) : List<TaskEntity> {
            var list : List<TaskEntity> = listOf()
            runBlocking {
                val job = GlobalScope.launch(Dispatchers.IO) {
                    list = if (dao.getTaskListBySectorId(id).isNotEmpty()) dao.getTaskListBySectorId(id) else list
                }
                job.join()
            }
            return list
        }
        @DelicateCoroutinesApi
        fun getFreshTaskList(id: Long, dao: TDao) : List<TaskEntity> {
            var list : List<TaskEntity> = listOf()
            runBlocking {
                val job = GlobalScope.launch(Dispatchers.IO) {
                    list = if (dao.getFreshTaskList(id, false, LocalDateTime.now()).isNotEmpty()) dao.getFreshTaskList(id, false, LocalDateTime.now()) else list
                }
                job.join()
            }
            return list
        }
        @DelicateCoroutinesApi
        fun getTaskById(dao: TDao, id: Long) : TaskEntity? {
            var taskEntity: TaskEntity? = null
            runBlocking {
                val job = GlobalScope.launch(Dispatchers.IO) {
                    taskEntity = dao.getTaskById(id)
                }
                job.join()
            }
            return taskEntity
        }
    }

}