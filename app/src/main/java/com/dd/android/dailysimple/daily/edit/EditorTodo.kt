package com.dd.android.dailysimple.daily.edit

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.di.systemLocale
import com.dd.android.dailysimple.common.utils.DateUtils.strYmdToLong
import com.dd.android.dailysimple.common.widget.recycler.ItemModelDiffCallback
import com.dd.android.dailysimple.daily.AppConst.NO_ID
import com.dd.android.dailysimple.daily.edit.observable.AlarmObservable
import com.dd.android.dailysimple.daily.edit.subtask.EditableTodoSubTask
import com.dd.android.dailysimple.daily.edit.subtask.TodoSubTaskAdapter
import com.dd.android.dailysimple.daily.edit.subtask.TodoSubTaskViewModel
import com.dd.android.dailysimple.daily.viewmodel.TodoViewModel
import com.dd.android.dailysimple.databinding.FragmentMakeAndEditBinding
import com.dd.android.dailysimple.db.data.Alarm
import com.dd.android.dailysimple.db.data.DailyTodo
import com.dd.android.dailysimple.db.data.DoneState.Companion.ONGOING
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "EditorTodo"

private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)


class EditorTodo(
    private val context: Context,
    private val bind: FragmentMakeAndEditBinding,
    private val lifecycleOwner: LifecycleOwner,
    viewModelStoreOwner: ViewModelStoreOwner
) : Editable {

    private val todoVm = ViewModelProvider(viewModelStoreOwner).get(TodoViewModel::class.java)
    private lateinit var subTaskVm: TodoSubTaskViewModel
    private lateinit var model: DailyTodo

    private val subTaskAdapter by lazy {
        TodoSubTaskAdapter(lifecycleOwner).apply { setHasStableIds(true) }
    }

    private val alarmObservable = AlarmObservable(Alarm())

    override var alarmTime: Long
        get() = alarmObservable.alarmTime
        set(value) {
            alarmObservable.alarmTime = value
        }

    private fun ensureAlarm(todoId: Long, alarm: Alarm?) = alarm?.also { it ->
        alarmObservable.alarm = it
        alarmObservable.isOn = true
        logD { "Todo(${todoId}) alarm : $it" }
    } ?: alarmObservable.alarm


    override fun bind(id: Long) {
        todoVm.getTodo(id).observe(lifecycleOwner, Observer { model ->
            this.model = (model ?: DailyTodo.create(
                context,
                start = todoVm.selectedDate.value!!
            )).apply {
                alarm = ensureAlarm(id, alarm)
            }

            bind.content = this.model.toEditContent()
            logD { "todoId : $id, model:${this.model}" }
        })

        bindSubTasks(id)
        bind.alarmModel = alarmObservable
        bind.featuers = EditFeatures(supportRepeat = false)
    }

    private fun bindSubTasks(todoId: Long) {
        with(bind.subTasks) {
            layoutManager = LinearLayoutManager(context)
            adapter = subTaskAdapter
        }

        if (todoId == NO_ID) return
        subTaskVm = TodoSubTaskViewModel(todoId)
        bind.subTask.setOnClickListener { subTaskVm.newEmptyTask() }

        subTaskVm.liveDataSubTasks.observe(lifecycleOwner, Observer { list ->
            val diffResult =
                DiffUtil.calculateDiff(ItemModelDiffCallback(subTaskAdapter.items, list))

            with(subTaskAdapter) {
                items.clear()
                items.addAll(list)
                diffResult.dispatchUpdatesTo(this)
            }
        })
    }

    override fun edit() {
        editTodoWith { newIds -> GlobalScope.launch { subTaskVm.saveTasks(newIds[0]) } }
    }

    private fun editTodoWith(block: (newIds: List<Long>) -> Unit) = todoVm.insertTodo(
        DailyTodo(
            id = model.id,
            title = bind.titleEditor.text.toString(),
            memo = bind.memoEditor.text.toString(),
            color = (bind.color.background as ColorDrawable).color,
            start = strYmdToLong(bind.startDate.text.toString(), systemLocale()),
            until = strYmdToLong(bind.endDate.text.toString(), systemLocale()),
            done = ONGOING,
            alarm = if (alarmObservable.isOn) alarmObservable.alarm else null
        )
    ).observeForever {
        block(it)
    }
}