package com.dd.android.dailysimple.daily.edit

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.di.systemLocale
import com.dd.android.dailysimple.common.utils.DateUtils.strYmdToLong
import com.dd.android.dailysimple.daily.edit.observable.AlarmObservable
import com.dd.android.dailysimple.daily.viewmodel.HabitViewModel
import com.dd.android.dailysimple.databinding.FragmentMakeAndEditBinding
import com.dd.android.dailysimple.db.data.Alarm
import com.dd.android.dailysimple.db.data.DailyHabit

private const val TAG = "EditorHabit"

private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class EditorHabit(
    private val context: Context,
    private val bind: FragmentMakeAndEditBinding,
    private val viewLifecycleOwner: LifecycleOwner,
    viewModelStoreOwner: ViewModelStoreOwner
) : Editable {

    private val habitVm = ViewModelProvider(viewModelStoreOwner).get(HabitViewModel::class.java)
    private lateinit var model: DailyHabit

    private val alarmObservable = AlarmObservable(Alarm())

    override var alarmTime: Long
        get() = alarmObservable.alarmTime
        set(value) {
            alarmObservable.alarmTime = value
        }

    override fun bind(id: Long) {
        habitVm.getHabit(id).observe(viewLifecycleOwner, Observer { model ->
            this.model =
                (model ?: DailyHabit.create(
                    context,
                    start = habitVm.selectedDate.value!!
                )).apply {
                    alarm = ensureAlarm(id, alarm)
                }
            bind.content = this.model.toEditContent()
            logD { "habitId : $id, model:${this.model}" }
        })
        bind.alarmModel = alarmObservable
        bind.features =
            EditFeatures(supportRepeat = true, supportAttachments = false, supportSubTasks = false)
    }

    private fun ensureAlarm(habitId: Long, alarm: Alarm?) = alarm?.also { it ->
        alarmObservable.alarm = it
        alarmObservable.isOn = true
        logD { "Habit(${habitId}) alarm : $it" }
    } ?: alarmObservable.alarm


    override fun edit() {
        habitVm.insert(
            DailyHabit(
                id = model.id,
                title = (bind.titleEditor.text.toString()),
                color = (bind.color.background as ColorDrawable).color,
                memo = (bind.memoEditor.text.toString()),
                start = strYmdToLong(bind.startDate.text.toString(), systemLocale()),
                until = strYmdToLong(bind.endDate.text.toString(), systemLocale()),
                alarm = if (alarmObservable.isOn) alarmObservable.alarm else null
            )
        )
    }
}