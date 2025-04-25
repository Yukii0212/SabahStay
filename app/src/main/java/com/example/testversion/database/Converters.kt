package com.example.testversion.database

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

object Converters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    @JvmStatic
    fun fromLocalDate(date: LocalDate?): String? = date?.format(formatter)

    @TypeConverter
    @JvmStatic
    fun toLocalDate(date: String?): LocalDate? = date?.let { LocalDate.parse(it, formatter) }
}
