package com.dd.android.dailysimple.daily.edit.subtask

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.dd.android.dailysimple.BR
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.widget.recycler.RecyclerViewAdapter2
import com.dd.android.dailysimple.common.widget.recycler.ViewHolder2
import com.dd.android.dailysimple.databinding.TodoSubTaskItemBinding

class TodoSubTaskViewHolder(parent: ViewGroup) :
    ViewHolder2<TodoSubTaskItemBinding, EditableTodoSubTask>(
        parent,
        R.layout.todo_sub_task_item,
        BR.itemModel
    )

class TodoSubTaskAdapter(lifecycleOwner: LifecycleOwner) : RecyclerViewAdapter2(lifecycleOwner) {
    override fun onCreateViewHolder2(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder2<out TodoSubTaskItemBinding, out EditableTodoSubTask> {
        return TodoSubTaskViewHolder(parent)
    }
}