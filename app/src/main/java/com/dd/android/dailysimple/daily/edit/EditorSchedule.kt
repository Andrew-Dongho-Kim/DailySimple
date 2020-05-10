package com.dd.android.dailysimple.daily.edit

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.daily.edit.observable.AlarmObservable
import com.dd.android.dailysimple.daily.viewmodel.ScheduleViewModel
import com.dd.android.dailysimple.databinding.FragmentMakeAndEditBinding
import com.dd.android.dailysimple.db.data.Alarm
import com.dd.android.dailysimple.db.data.DailySchedule

private const val TAG = "EditorSchedule"

private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class EditorSchedule(
    private val context: Context,
    private val bind: FragmentMakeAndEditBinding,
    private val viewLifecycleOwner: LifecycleOwner,
    viewModelStoreOwner: ViewModelStoreOwner
) : Editable {

    private lateinit var model: DailySchedule

    private val alarmObservable = AlarmObservable(Alarm())

    private val scheduleVm =
        ViewModelProvider(viewModelStoreOwner).get(ScheduleViewModel::class.java)

    override var alarmTime: Long
        get() = alarmObservable.alarmTime
        set(value) {
            alarmObservable.alarmTime = value
        }

    override fun bind(id: Long) {
//        scheduleVm.getSchedule(id).observe(viewLifecycleOwner, Observer { model ->
//            this.model = (model ?: DailySchedule.create(context)).apply {
//                alarm = ensureAlarm(id, alarm)
//            }
//            bind.content = this.model.toEditContent()
//            logD { "habitId : $id, model:${this.model}" }
//        })
        bind.alarmModel = alarmObservable
    }

    private fun ensureAlarm(habitId: Long, alarm: Alarm?) = alarm?.also { it ->
        alarmObservable.alarm = it
        alarmObservable.isOn = true
        logD { "Habit(${habitId}) alarm : $it" }
    } ?: alarmObservable.alarm

    override fun edit() {
        TODO("Not yet implemented")
    }
}