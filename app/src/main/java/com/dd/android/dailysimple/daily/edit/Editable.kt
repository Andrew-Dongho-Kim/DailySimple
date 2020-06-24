package com.dd.android.dailysimple.daily.edit

import androidx.annotation.IntDef
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.di.getString
import com.dd.android.dailysimple.daily.edit.EditType.Companion.HABIT
import com.dd.android.dailysimple.daily.edit.EditType.Companion.SCHEDULE
import com.dd.android.dailysimple.daily.edit.EditType.Companion.TODO
import com.dd.android.dailysimple.db.data.DailyHabit
import com.dd.android.dailysimple.db.data.DailySchedule
import com.dd.android.dailysimple.db.data.DailyTodo

@IntDef(SCHEDULE, TODO, HABIT)
annotation class EditType {
    companion object {
        const val SCHEDULE = 0
        const val TODO = 1
        const val HABIT = 2
    }
}

data class EditFeatures(
    val supportAlarm: Boolean = true,
    val supportRepeat: Boolean = false,
    val supportSubTasks: Boolean = true,
    val supportAttachments: Boolean = true
)

data class EditContent(
    val type: String,
    val title: String,
    val formattedStart: String,
    val formattedEnd: String,
    val color: Int = 0x00000000,
    val memo: String? = null
)

interface Editable {
    var alarmTime: Long

    fun bind(id: Long)

    fun edit()
}

fun DailyHabit.toEditContent(): EditContent =
    EditContent("#${getString(R.string.habit)}", title, formattedStart, formattedEnd, color, memo)

fun DailyTodo.toEditContent(): EditContent =
    EditContent("#${getString(R.string.todo)}", title, formattedStart, formattedEnd, memo = memo)

fun DailySchedule.toEditContent(): EditContent =
    EditContent(
        "#${getString(R.string.schedule)}",
        title,
        formattedStart,
        formattedEnd,
        color = color,
        memo = memo
    )