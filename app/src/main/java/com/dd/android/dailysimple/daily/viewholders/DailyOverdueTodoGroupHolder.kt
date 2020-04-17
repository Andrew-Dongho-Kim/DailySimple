package com.dd.android.dailysimple.daily.viewholders

import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.di.appResources
import com.dd.android.dailysimple.common.recycler.ItemModel
import com.dd.android.dailysimple.common.recycler.ViewHolder2
import com.dd.android.dailysimple.databinding.DailyOverdueTodoItemBinding
import com.dd.android.dailysimple.db.data.DailyTodo

class DailyOverdueTodoGroupHolder(parent: ViewGroup) :
    ViewHolder2<DailyOverdueTodoItemBinding, DailyOverdueTodoGroup>(
        parent,
        R.layout.daily_overdue_todo_item,
        BR.itemModel
    ) {
    init {
        itemClickListener = ::onClick
    }

    private fun onClick(view: View) {
        model?.let {
            it.isExpanded = !it.isExpanded
            bind.expandIcon.animate().rotation(it.iconDegree)
        }
    }
}


data class DailyOverdueTodoGroup(
    override val id: Long,
    private val overdueTodo: List<DailyTodo>
) : ItemModel {

    private val size = overdueTodo.size

    var isExpanded = false
        set(value) {
            field = value
            iconDegree = if (field) ROT_EXPANDED else ROT_COLLAPSED
        }

    var iconDegree = ROT_COLLAPSED

    val message: String =
        appResources.getQuantityString(R.plurals.plurals_overdue_task_message, size, size)


    companion object {
        private const val ROT_EXPANDED = 90f

        private const val ROT_COLLAPSED = 0f
    }
}