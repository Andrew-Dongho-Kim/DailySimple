package com.dd.android.dailysimple.db.data

import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.room.*
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.di.appContext
import com.dd.android.dailysimple.common.di.getString
import com.dd.android.dailysimple.common.di.systemLocale
import com.dd.android.dailysimple.common.recycler.ItemModel
import com.dd.android.dailysimple.common.utils.DateUtils.delayRemain
import com.dd.android.dailysimple.common.utils.DateUtils.msDateOnlyFrom
import com.dd.android.dailysimple.common.utils.DateUtils.toRemain
import com.dd.android.dailysimple.common.utils.DateUtils.toYMD


@Entity(tableName = "daily_todo")
data class DailyTodo(
    @PrimaryKey(autoGenerate = true) override val id: Long,
    var title: String,
    var memo: String,
    var start: Long,
    var until: Long,
    var done: Int,
    @Embedded var alarm: Alarm? = null
) : ItemModel {

    @Ignore
    val isOverdue = System.currentTimeMillis() > until

    @Ignore
    val formattedStart = toYMD(start, systemLocale())

    @Ignore
    val formattedEnd = toYMD(until, systemLocale())

    @Ignore
    private val remain = liveData {
        var left: Long
        do {
            left = until - System.currentTimeMillis()
            emit(left)
            delayRemain(left)
        } while (left > 0)
    }

    @Ignore
    val timeRemain = Transformations.map(remain) { remain ->
        if (done == ONGOING) {
            toRemain(appContext, remain)
        } else {
            getString(R.string.done)
        }
    }

    fun toggleDone() {
        done = if (done == ONGOING) DONE else ONGOING
    }

    companion object {
        fun create() = DailyTodo(
            id = NO_ID,
            title = "",
            memo = "",
            start = msDateOnlyFrom(),
            until = msDateOnlyFrom(DEFAULT_END),
            done = ONGOING
        )

        private const val NO_ID = 0L

        private const val DEFAULT_END = 1

        const val ONGOING = 0
        const val DONE = 1
    }
}


@Entity(tableName = "daily_todo_sub_job")
data class DailyTodoSubJob(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sub_job_id")
    var subJobId: Long,
    @ForeignKey(
        entity = DailyTodo::class,
        parentColumns = ["id"],
        childColumns = ["daily_todo_id"],
        onDelete = ForeignKey.CASCADE
    )
    @ColumnInfo(name = "daily_todo_id")
    var dailyTodoId: Long,
    var title: String
)


