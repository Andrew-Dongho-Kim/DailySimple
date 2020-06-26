package com.dd.android.dailysimple.daily

import androidx.annotation.IntDef
import com.dd.android.dailysimple.daily.DailyViewType.Companion.AUTHORITY_ITEM
import com.dd.android.dailysimple.daily.DailyViewType.Companion.EMPTY_ITEM
import com.dd.android.dailysimple.daily.DailyViewType.Companion.HABIT_ITEM
import com.dd.android.dailysimple.daily.DailyViewType.Companion.OVERDUE_TODO_GROUP
import com.dd.android.dailysimple.daily.DailyViewType.Companion.SCHEDULE_ITEM
import com.dd.android.dailysimple.daily.DailyViewType.Companion.SIMPLE_HEADER
import com.dd.android.dailysimple.daily.DailyViewType.Companion.TODO_ITEM
import com.dd.android.dailysimple.daily.viewholders.DailyAuthorityItem
import com.dd.android.dailysimple.daily.viewholders.DailyEmptyItem
import com.dd.android.dailysimple.daily.viewholders.DailySimpleHeaderItem
import com.dd.android.dailysimple.daily.viewholders.DailyTodoGroup
import com.dd.android.dailysimple.db.data.DailyHabitWithCheckStatus
import com.dd.android.dailysimple.db.data.DailySchedule
import com.dd.android.dailysimple.db.data.DailyTodo


@IntDef(
    SIMPLE_HEADER,
    EMPTY_ITEM,
    AUTHORITY_ITEM,
    SCHEDULE_ITEM,
    TODO_ITEM,
    HABIT_ITEM,
    OVERDUE_TODO_GROUP
)
annotation class DailyViewType {
    companion object {
        const val SIMPLE_HEADER = -1
        const val EMPTY_ITEM = 0
        const val AUTHORITY_ITEM = 1
        const val SCHEDULE_ITEM = 2
        const val TODO_ITEM = 3
        const val HABIT_ITEM = 4
        const val OVERDUE_TODO_GROUP = 5
    }
}

val UNKNOWN_VIEW_TYPE = -10

data class IdBase(val viewType: Int, val idBase: Long)

val IdMap = mapOf(
    Pair(DailySimpleHeaderItem::class.java, IdBase(SIMPLE_HEADER, -10L)),
    Pair(DailyEmptyItem::class.java, IdBase(EMPTY_ITEM, 10L)),
    Pair(DailyAuthorityItem::class.java, IdBase(AUTHORITY_ITEM, 50L)),
    Pair(DailySchedule::class.java, IdBase(SCHEDULE_ITEM, 100L)),
    Pair(DailyTodo::class.java, IdBase(TODO_ITEM, 1000000000L)),
    Pair(DailyHabitWithCheckStatus::class.java, IdBase(HABIT_ITEM, 2000000000L)),
    Pair(DailyTodoGroup::class.java, IdBase(OVERDUE_TODO_GROUP, 3000000000L))
)