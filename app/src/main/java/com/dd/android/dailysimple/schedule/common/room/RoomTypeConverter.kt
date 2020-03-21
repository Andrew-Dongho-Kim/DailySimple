package com.dd.android.dailysimple.schedule.common.room

import androidx.room.TypeConverter
import java.util.*

class RoomTypeConverter {

    companion object {

        @TypeConverter
        fun timeStampToDate(value: Long) = Date(value)

        @TypeConverter
        fun dateToTimeStamp(date: Date) = date.time
    }
}