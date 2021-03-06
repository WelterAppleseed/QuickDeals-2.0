package com.example.fffaaaa.room.enitites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fffaaaa.room.daos.SDao
import kotlinx.coroutines.*


@Entity(tableName = "sectors")
data class SectorEntity(
    @PrimaryKey
    var id: Long,
    @ColumnInfo(name = "icon")
    var icon: Int,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "rem_count")
    var remCount: Int
) {

    companion object {
        val BY_ID : Comparator<SectorEntity> = Comparator { o1: SectorEntity, o2: SectorEntity ->  o1.id.compareTo(o2.id)}
        val BY_ALPHABET : Comparator<SectorEntity> = Comparator { o1: SectorEntity, o2: SectorEntity ->  o1.title.compareTo(o2.title)}
        val BY_TASK_COUNT : Comparator<SectorEntity> = Comparator { o1: SectorEntity, o2: SectorEntity ->  o2.remCount.compareTo(o1.remCount)}
        @DelicateCoroutinesApi
        fun insert(dao: SDao, sectorEntity: SectorEntity) {
            runBlocking {
                val job = GlobalScope.launch(Dispatchers.IO) {
                    dao.insert(sectorEntity)
                }
                job.join()
            }
        }

        @DelicateCoroutinesApi
        fun update(dao: SDao, sectorEntity: SectorEntity) {
            runBlocking {
                val job = GlobalScope.launch(Dispatchers.IO) {
                    dao.update(sectorEntity)
                }
                job.join()
            }
        }

        @DelicateCoroutinesApi
        fun delete(dao: SDao, sectorEntity: SectorEntity) {
            runBlocking {
                val job = GlobalScope.launch(Dispatchers.IO) {
                    dao.delete(sectorEntity)
                }
                job.join()
            }
        }

        @DelicateCoroutinesApi
        fun findSectorById(dao: SDao, id: Long): SectorEntity? {
            var sectorEntity: SectorEntity? = null
            runBlocking {
                val job = GlobalScope.launch(Dispatchers.IO) {
                    sectorEntity = dao.findSectorById(id)
                }
                job.join()
            }
            return sectorEntity
        }


        @DelicateCoroutinesApi
        fun getAllSectors(dao: SDao): ArrayList<SectorEntity> {
           var list: List<SectorEntity> = arrayListOf()
            runBlocking {
                val job = GlobalScope.launch(Dispatchers.IO) {
                        list = dao.getAllSectors()
                }
                job.join()
            }
            return ArrayList(list)
        }

        @DelicateCoroutinesApi
        fun getAllSectorTitles(dao: SDao): ArrayList<String> {
            val list: ArrayList<String> = arrayListOf()
            runBlocking {
                val job = GlobalScope.launch(Dispatchers.IO) {
                        for (entity in dao.getAllSectors()) {
                            list.add(entity.title)
                        }
                }
                job.join()
            }
            return list
        }

        @DelicateCoroutinesApi
        fun deleteAll(dao: SDao) {
            runBlocking {
                val job = GlobalScope.launch(Dispatchers.IO) {
                    dao.clear()
                }
                job.join()
            }
        }
        @DelicateCoroutinesApi
        fun getSectorTaskCount(dao: SDao, title: String): Int {
            var taskCount = -1
            runBlocking {
                val job = GlobalScope.launch(Dispatchers.IO) {
                    taskCount = dao.getSectorTaskCount(title)
                }
                job.join()
            }
            return taskCount
        }
        @DelicateCoroutinesApi
        fun getSectorsSortedByName(dao: SDao, isAsc : Int?): ArrayList<String> {
            var list: ArrayList<String> = arrayListOf()
            runBlocking {
                val job = GlobalScope.launch(Dispatchers.IO) {
                    list = if (dao.getSectorsSortedByName(isAsc)
                            .isNotEmpty()
                    ) ArrayList(dao.getSectorsSortedByName(isAsc)) else list
                }
                job.join()
            }
            return list
        }
    }
}
