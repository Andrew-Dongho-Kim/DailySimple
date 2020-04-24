package com.dd.android.dailysimple.daily.viewholders

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.recycler.ViewHolder2
import com.dd.android.dailysimple.daily.DailyOverduePopup
import com.dd.android.dailysimple.daily.viewmodel.TodoViewModel
import com.dd.android.dailysimple.databinding.DailyTodoItemBinding
import com.dd.android.dailysimple.db.data.DailyTodo

class DailyTodoItemHolder(
    parent: ViewGroup,
    private val viewModelStoreOwner: ViewModelStoreOwner
) : ViewHolder2<DailyTodoItemBinding, DailyTodo>(
    parent,
    R.layout.daily_todo_item,
    BR.itemModel
) {
    private val todoVm = ViewModelProvider(viewModelStoreOwner).get(
        TodoViewModel::class.java
    )

    init {
        itemClickListener = ::onClick
    }

    private fun onClick(view: View) {
        model?.let {
            if (it.isOverdue) {
                DailyOverduePopup(
                    view.context,
                    viewModelStoreOwner = viewModelStoreOwner,
                    todoId = it.id
                ).showAsDropDown(
                    view,
                    50,
                    0, Gravity.TOP or Gravity.START
                )
            } else {
                it.toggleDone()
                todoVm.update(it)
            }
        }
    }
}