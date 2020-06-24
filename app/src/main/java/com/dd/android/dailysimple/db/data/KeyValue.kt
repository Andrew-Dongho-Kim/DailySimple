package com.dd.android.dailysimple.db.data

import androidx.annotation.IntDef
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dd.android.dailysimple.db.data.KeyValue.Type.Companion.BOOLEAN
import com.dd.android.dailysimple.db.data.KeyValue.Type.Companion.DOUBLE
import com.dd.android.dailysimple.db.data.KeyValue.Type.Companion.FLOAT
import com.dd.android.dailysimple.db.data.KeyValue.Type.Companion.INT
import com.dd.android.dailysimple.db.data.KeyValue.Type.Companion.STRING


@Entity(tableName = "key_value")
data class KeyValue(
    @PrimaryKey val key: String,
    @Type val type: Int,
    val value: String
) {

    @IntDef(BOOLEAN, INT, FLOAT, DOUBLE, STRING)
    annotation class Type {
        companion object {
            const val BOOLEAN = 0
            const val INT = 1
            const val FLOAT = 2
            const val DOUBLE = 3
            const val STRING = 4
        }
    }
}