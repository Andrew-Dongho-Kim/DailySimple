package com.dd.android.dailysimple.db.data

import android.content.Context
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import androidx.room.*
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.di.getColor
import com.dd.android.dailysimple.common.di.getString
import com.dd.android.dailysimple.common.di.systemLocale
import com.dd.android.dailysimple.common.utils.DateUtils.MS_DAY
import com.dd.android.dailysimple.common.utils.DateUtils.MS_HOUR
import com.dd.android.dailysimple.common.utils.DateUtils.MS_MINUTE
import com.dd.android.dailysimple.common.utils.DateUtils.MS_SECOND
import com.dd.android.dailysimple.common.utils.DateUtils.msDateFrom
import com.dd.android.dailysimple.common.utils.DateUtils.msFrom
import com.dd.android.dailysimple.common.utils.DateUtils.toYMD
import com.dd.android.dailysimple.common.utils.htmlTextColor
import com.dd.android.dailysimple.common.widget.recycler.ItemModel
import com.dd.android.dailysimple.daily.ChildItemModel
import com.dd.android.dailysimple.db.data.DoneState.Companion.ONGOING
import com.dd.android.dailysimple.db.data.DoneState.Companion.isDone
import kotlinx.coroutines.delay
import kotlin.math.abs

class DoneState {
    companion object {
        const val ONGOING = 0L

        @JvmStatic
        fun isDone(doneState: Long): Boolean = doneState != ONGOING
    }
}

@Entity(tableName = "daily_todo")
data class DailyTodo(
    @PrimaryKey(autoGenerate = true)
    override val id: Long,
    var title: String,
    var memo: String,
    var color: Int,
    var start: Long,
    var until: Long,
    var done: Long,
    @Embedded var alarm: Alarm? = null
) : ChildItemModel {

    // @formatter:off
    @Ignore override var hasParent = false

    @Ignore val isOverdue = msDateFrom() >= until
    @Ignore val isUpcoming = msDateFrom(1) <= start

    @Ignore val formattedStart = toYMD(start, systemLocale())
    @Ignore val formattedEnd = toYMD(until, systemLocale())
    // @formatter:on

    @Ignore
    private val remain = liveData {
        val time = if (isUpcoming) start else until
        var left: Long
        do {
            left = abs(time - System.currentTimeMillis())
            emit(left)
            delayRemain(left)
        } while (!isDone(done))
    }

    @Ignore
    val timeRemain = Transformations.map(remain) { remain ->
        when {
            isDone(done) -> toDoneText()
            isOverdue -> toTimeText(remain, R.string.past, R.color.apple)
            isUpcoming -> toTimeText(remain, R.string.after, R.color.appPrimary)
            else -> toTimeText(remain, R.string.left)
        }
    }

    fun toggleDone() {
        done = if (done == ONGOING) msFrom() else ONGOING
    }

    private suspend fun delayRemain(time: Long) {
        delay(
            time % when {
                time < MS_MINUTE -> MS_SECOND
                time < MS_HOUR -> MS_MINUTE
                time < MS_DAY -> MS_HOUR
                else -> MS_DAY
            }
        )
    }

    private fun toTimeText(
        time: Long,
        @StringRes postfixResId: Int,
        @ColorRes colorResId: Int = 0
    ): Spanned {
        val timeText =
            String.format("%d${getString(hmsResource(time))} ${getString(postfixResId)}", hms(time))

        return if (colorResId == 0) {
            SpannableString(timeText)
        } else {
            Html.fromHtml(htmlTextColor(timeText, getColor(colorResId)))
        }
    }

    private fun toDoneText(): Spanned {
        return Html.fromHtml(htmlTextColor(getString(R.string.done), getColor(R.color.appPrimary)))
    }

    private fun hms(time: Long) =
        time / when {
            time < MS_MINUTE -> MS_SECOND
            time < MS_HOUR -> MS_MINUTE
            time < MS_DAY -> MS_HOUR
            else -> MS_DAY
        }

    private fun hmsResource(time: Long) =
        when {
            time < MS_MINUTE -> R.string.abb_second
            time < MS_HOUR -> R.string.abb_minute
            time < MS_DAY -> R.string.abb_hour
            else -> R.string.abb_day
        }

    companion object {
        fun create(context:Context) = DailyTodo(
            id = NO_ID,
            title = "",
            memo = "",
            color = ContextCompat.getColor(context, R.color.appPrimary),
            start = msDateFrom(),
            until = msDateFrom(DEFAULT_END),
            done = ONGOING
        )

        private const val NO_ID = 0L
        private const val DEFAULT_END = 1
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
    var done: Long = ONGOING
) : ItemModel


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
