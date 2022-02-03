package com.example.fffaaaa.room.converter

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset

class DateConverter {
    @TypeConverter
    fun toDate(dateLong: Long) : LocalDateTime {
      return LocalDateTime.ofEpochSecond(dateLong, 0, ZoneOffset.UTC)
    }
    @TypeConverter
    fun fromDate(date: LocalDateTime) : Long {
        return date.toEpochSecond(ZoneOffset.UTC)
    }

}