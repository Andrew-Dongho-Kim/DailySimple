package com.dd.android.dailysimple.daily.edit

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.LinearLayoutManager
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.di.systemLocale
import com.dd.android.dailysimple.common.utils.DateUtils.strYmdToLong
import com.dd.android.dailysimple.daily.AppConst.NO_ID
import com.dd.android.dailysimple.daily.edit.observable.AlarmObservable
import com.dd.android.dailysimple.daily.edit.subtask.EditableTodoSubTask
import com.dd.android.dailysimple.daily.edit.subtask.TodoSubTaskAdapter
import com.dd.android.dailysimple.daily.viewmodel.TodoViewModel
import com.dd.android.dailysimple.databinding.FragmentMakeAndEditBinding
import com.dd.android.dailysimple.db.data.Alarm
import com.dd.android.dailysimple.db.data.DailyTodo
import com.dd.android.dailysimple.db.data.DoneState.Companion.ONGOING
import com.dd.android.dailysimple.db.data.TodoSubTask

private const val TAG = "EditorTodo"

private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)


class EditorTodo(
    private val context: Context,
    private val bind: FragmentMakeAndEditBinding,
    private val lifecycleOwner: LifecycleOwner,
    viewModelStoreOwner: ViewModelStoreOwner
) : Editable {

    private val todoVm = ViewModelProvider(viewModelStoreOwner).get(TodoViewModel::class.java)
    private lateinit var model: DailyTodo
    private var id = 0L

    private val alarmObservable = AlarmObservable(Alarm())

    private val subTaskAdapter by lazy {
        TodoSubTaskAdapter(lifecycleOwner).apply { setHasStableIds(true) }
    }
    private val subTasks = mutableListOf<EditableTodoSubTask>()

    override var alarmTime: Long
        get() = alarmObservable.alarmTime
        set(value) {
            alarmObservable.alarmTime = value
        }

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

        // TODO DataBinding
        bind.subTask.setOnClickListener {
            EditableTodoSubTask(
                TodoSubTask(id++, todoId, ""),
                subTasks.size,
                this::onRequestRemoveSubJob,
                true
            ).run {
                subTasks.add(this)
                subTaskAdapter.items.add(this)
            }
            subTaskAdapter.notifyItemInserted(subTasks.size - 1)
            logD { "Sub Task Added : $id" }
        }

        // TODO DiffUtil
        if (todoId == NO_ID) return
        todoVm.getSubTasks(todoId).observe(lifecycleOwner, Observer { list ->
            subTasks.clear()
            subTasks.addAll(list.mapIndexed { index, task ->
                EditableTodoSubTask(
                    task,
                    index,
                    this::onRequestRemoveSubJob
                )
            })

            id = subTasks.map { it.id + 1 }.max() ?: NO_ID
            with(subTaskAdapter) {
                items.clear()
                items.addAll(subTasks)
                notifyDataSetChanged()
            }
        })
    }

    private fun ensureAlarm(todoId: Long, alarm: Alarm?) = alarm?.also { it ->
        alarmObservable.alarm = it
        alarmObservable.isOn = true
        logD { "Todo(${todoId}) alarm : $it" }
    } ?: alarmObservable.alarm

    private fun onRequestRemoveSubJob(editable: EditableTodoSubTask) {
        if (editable.task.title.isEmpty()) {
            if (editable.added) {

            } else {
                todoVm.deleteSubTask(editable.id)
            }
        }
        logD { "onRequestRemoveSubJob : $editable" }
    }

    override fun edit() {
        editTodoWith { newIds ->
            subTasks
                .filter { it.edited && it.task.title.isNotEmpty() }
                .forEach {
                    with(it.task) {
                        todoId = newIds[0]
                        id = if (it.added) 0L else id
                        todoVm.insertSubTask(this)
                    }
                    logD { "SubTask(inserted) : $it" }
                }
        }
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