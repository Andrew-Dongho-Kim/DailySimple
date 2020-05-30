package com.dd.android.dailysimple.db.data

import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.room.*
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.di.appContext
import com.dd.android.dailysimple.common.di.getString
import com.dd.android.dailysimple.common.di.systemLocale
import com.dd.android.dailysimple.common.utils.DateUtils.delayRemain
import com.dd.android.dailysimple.common.utils.DateUtils.msDateOnlyFrom
import com.dd.android.dailysimple.common.utils.DateUtils.toRemain
import com.dd.android.dailysimple.common.utils.DateUtils.toYMD
import com.dd.android.dailysimple.common.widget.recycler.ItemModel
import com.dd.android.dailysimple.daily.ChildItemModel


@Entity(tableName = "daily_todo")
data class DailyTodo(
    @PrimaryKey(autoGenerate = true) override val id: Long,
    var title: String,
    var memo: String,
    var start: Long,
    var until: Long,
    var done: Int,
    @Embedded var alarm: Alarm? = null
) : ChildItemModel {

    @Ignore
    override var hasParent = false

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


@Entity(tableName = "todo_sub_task")
data class TodoSubTask(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    override var id: Long,
    @ForeignKey(
        entity = DailyTodo::class,
        parentColumns = ["id"],
        childColumns = ["todo_id"],
        onDelete = ForeignKey.CASCADE
    )
    @ColumnInfo(name = "todo_id")
    var todoId: Long,
    var title: String,
    var state: Int = ONGOING
) : ItemModel {

    companion object {
        const val ONGOING = 0
        const val DONE = 1
    }
}


@Entity(tableName = "todo_attachment")
data class TodoAttachment(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long,
    @ForeignKey(
        entity = DailyTodo::class,
        parentColumns = ["id"],
        childColumns = ["todo_id"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )
    @ColumnInfo(name = "todo_id")
    val todoId: Long,
    val uri: String
)
