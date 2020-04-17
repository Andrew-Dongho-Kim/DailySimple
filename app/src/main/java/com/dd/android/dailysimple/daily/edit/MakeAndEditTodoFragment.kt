package com.dd.android.dailysimple.daily.edit

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.BaseFragment
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.utils.DateUtils.msDateOnlyFrom
import com.dd.android.dailysimple.daily.edit.observable.AlarmObservable
import com.dd.android.dailysimple.daily.viewmodel.TodoViewModel
import com.dd.android.dailysimple.databinding.FragmentMakeDailyTodoBinding
import com.dd.android.dailysimple.db.data.Alarm
import com.dd.android.dailysimple.db.data.DailyTodo
import com.dd.android.dailysimple.db.data.DailyTodo.Companion.ONGOING

private const val TAG = "MakeAndEdit"

private const val ARG_ID = "todoId"

private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class MakeAndEditTodoFragment : BaseFragment<FragmentMakeDailyTodoBinding>() {

    private val alarmObservable = AlarmObservable(Alarm())

    private val viewModel by viewModels<TodoViewModel>()

    override val layout = R.layout.fragment_make_daily_todo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpToolbar()
        setUpModel()
    }

    private fun setUpModel() {
        val todoId = arguments?.get(ARG_ID) as Long?

        todoId?.let {
            viewModel.getTodo(it).observe(viewLifecycleOwner, Observer { todoModel ->
                bind.todoModel = todoModel
                todoModel?.alarm?.let { alarm ->
                    alarmObservable.alarm = alarm
                    alarmObservable.isOn = true
                    logD { "Habit(${todoId}) alarm : $alarm" }
                }
            })
        }
//        bind.alarmModel = alarmObservable
    }

    private fun setUpToolbar() {
        activity.setSupportActionBar(bind.toolbar)
        activity.supportActionBar?.apply {
            setDisplayShowCustomEnabled(false)
            setDisplayShowTitleEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            title = activity.getString(R.string.make_a_habit)
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.create_daily_schedule_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.done -> {
                viewModel.insert(
                    DailyTodo(
                        id = bind.todoModel?.id ?: 0,
                        title = bind.titleEditor.text.toString(),
//                        color = bind.color.imageTintList!!.defaultColor,
                        memo = bind.memoEditor.text.toString(),
                        start = msDateOnlyFrom(),
                        until = msDateOnlyFrom(1),
                        done = bind.todoModel?.done ?: ONGOING,
                        alarm = alarmObservable.alarm
                    )
                )
                logD { "Alarm  : ${alarmObservable.alarm}" }
                popBackStack()
                true
            }
            R.id.cancel -> {
                popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}