package com.dd.android.dailysimple.daily.edit

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.di.systemLocale
import com.dd.android.dailysimple.common.utils.DateUtils
import com.dd.android.dailysimple.common.utils.DateUtils.strYmdToLong
import com.dd.android.dailysimple.daily.edit.observable.AlarmObservable
import com.dd.android.dailysimple.daily.viewmodel.TodoViewModel
import com.dd.android.dailysimple.databinding.FragmentMakeAndEditBinding
import com.dd.android.dailysimple.db.data.Alarm
import com.dd.android.dailysimple.db.data.DailyTodo
import com.dd.android.dailysimple.db.data.DailyTodo.Companion.ONGOING

private const val TAG = "EditorTodo"

private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class EditorTodo(
    private val bind: FragmentMakeAndEditBinding,
    private val viewLifecycleOwner: LifecycleOwner,
    viewModelStoreOwner: ViewModelStoreOwner
) : Editable {

    private lateinit var model: DailyTodo

    private val alarmObservable = AlarmObservable(Alarm())

    private val todoVm = ViewModelProvider(viewModelStoreOwner).get(TodoViewModel::class.java)

    override var alarmTime: Long
        get() = alarmObservable.alarmTime
        set(value) {
            alarmObservable.alarmTime = value
        }

    override fun bind(id: Long) {
        todoVm.getTodo(id).observe(viewLifecycleOwner, Observer { model ->
            this.model = (model ?: DailyTodo.create()).apply {
                alarm = ensureAlarm(id, alarm)
            }
            bind.content = this.model.toEditContent()
            logD { "todoId : $id, model:${this.model}" }
        })
        bind.alarmModel = alarmObservable
    }

    private fun ensureAlarm(habitId: Long, alarm: Alarm?) = alarm?.also { it ->
        alarmObservable.alarm = it
        alarmObservable.isOn = true
        logD { "Todo(${habitId}) alarm : $it" }
    } ?: alarmObservable.alarm

    override fun edit() {
        todoVm.insert(
            DailyTodo(
                id = model.id,
                title = (bind.titleEditor.text.toString()),
                memo = (bind.memoEditor.text.toString()),
                start = strYmdToLong(bind.startDate.text.toString(), systemLocale()),
                until = strYmdToLong(bind.endDate.text.toString(), systemLocale()),
                done = ONGOING,
                alarm = alarmObservable.alarm
            )
        )
    }
}