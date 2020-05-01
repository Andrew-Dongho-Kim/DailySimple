package com.dd.android.dailysimple.db.data

import android.content.Context
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.*
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.di.getString
import com.dd.android.dailysimple.common.di.systemLocale
import com.dd.android.dailysimple.common.recycler.ItemModel
import com.dd.android.dailysimple.common.utils.DateUtils.msDateOnlyFrom
import com.dd.android.dailysimple.common.utils.DateUtils.toYMD
import com.dd.android.dailysimple.db.data.DailyHabit.CheckTerm.*

class DailyHabitTypeConverters {
    @TypeConverter
    fun fromTermsToInt(term: DailyHabit.CheckTerm?) = term?.ordinal

    @TypeConverter
    fun fomIntToTerms(value: Int?) = value?.let { values()[it] }
}


@Entity(tableName = "daily_habit")
data class DailyHabit(
    @PrimaryKey(autoGenerate = true) override val id: Long,
    @ForeignKey(
        entity = Plan::class,
        parentColumns = ["id"],
        childColumns = ["planId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )
    val groupId: Long = UNKNOWN_ID,
    var title: String,
    var memo: String? = null,
    @ColumnInfo(name = "check_term") var checkTerm: CheckTerm = DAY,
    var checkLimits: Int = 1,
    var color: Int,
    var start: Long,
    var until: Long? = null,
    @Embedded var alarm: Alarm? = null
) : ItemModel {

    @Ignore
    val formattedStart = toYMD(start, systemLocale())

    @Ignore
    val formattedEnd =
        until?.let { toYMD(it, systemLocale()) } ?: getString(R.string.infinite)


    enum class CheckTerm {
        DAY,
        WEEK,
        MONTH;
    }

    companion object {
        private const val NO_ID = 0L

        private const val DEFAULT_END = 66

        fun create(context: Context) = DailyHabit(
            id = NO_ID,
            title = "",
            color = getColor(context, R.color.appPrimary),
            start = msDateOnlyFrom(),
            until = msDateOnlyFrom(DEFAULT_END)
        )
    }
}


@Entity(tableName = "check_status", primaryKeys = ["date", "habit_id"])
data class CheckStatus(
    val date: Long,
    @ForeignKey(
        entity = DailyHabit::class,
        parentColumns = ["id"],
        childColumns = ["habit_id"],
        onDelete = ForeignKey.CASCADE
    )
    @ColumnInfo(name = "habit_id") val habitId: Long,
    @ColumnInfo(name = "checked_count") val checkedCount: Int
) : ItemModel {

    @Ignore
    override val id = date
}


data class DailyHabitWithCheckStatus(
    val habit: DailyHabit,
    val checkedList: LiveData<List<CheckStatus>>
) : ItemModel {

    @Ignore
    override val id = habit.id

    @Ignore
    val terms = getString(
        when (habit.checkTerm) {
            DAY -> R.string.abb_day
            WEEK -> R.string.abb_week
            MONTH -> R.string.abb_month
        }
    )

    @Ignore
    val status = Transformations.map(checkedList) {
        "${it.size}/${habit.checkLimits}"
    }
}