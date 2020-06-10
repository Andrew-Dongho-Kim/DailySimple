package com.dd.android.dailysimple.daily.viewholders

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import com.dd.android.dailysimple.HomeFragmentDirections.Companion.homeToMakeAndEdit
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.widget.recycler.ViewHolder2
import com.dd.android.dailysimple.daily.DailyOverduePopup
import com.dd.android.dailysimple.daily.simplecalendar.SelectedDateInfo
import com.dd.android.dailysimple.daily.edit.EditType
import com.dd.android.dailysimple.daily.viewmodel.TodoViewModel
import com.dd.android.dailysimple.databinding.DailyTodoItemBinding
import com.dd.android.dailysimple.db.data.DailyTodo

class DailyTodoItemHolder(
    parent: ViewGroup,
    private val viewModelStoreOwner: ViewModelStoreOwner,
    private val navController: NavController
) : ViewHolder2<DailyTodoItemBinding, DailyTodo>(
    parent,
    R.layout.daily_todo_item,
    BR.itemModel
) {

    private val todoVm = ViewModelProvider(viewModelStoreOwner).get(
        TodoViewModel::class.java
    )

    init {
        itemClickListener = ::onItemClick
        bind.check.setOnClickListener(::onToggleDone)
        bind.dateInfo =
            SelectedDateInfo(
                todoVm.selectedDate
            )
    }

    private fun onItemClick(view: View) {
        navController.navigate(homeToMakeAndEdit(model!!.id, EditType.TODO))
    }

    private fun onToggleDone(view: View) {
        model?.let { todo -> todoVm.updateTodo(todo.also { it.toggleDone() }) }
    }
}