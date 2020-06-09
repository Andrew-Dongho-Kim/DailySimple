package com.dd.android.dailysimple.daily.edit.subtask

import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_PREVIOUS
import com.dd.android.dailysimple.common.utils.DateUtils.msFrom
import com.dd.android.dailysimple.common.widget.recycler.ItemModel
import com.dd.android.dailysimple.db.data.DoneState
import com.dd.android.dailysimple.db.data.TodoSubTask

data class EditableTodoSubTask(
    val task: TodoSubTask,
    val position: Int,
    var requestRemove: ((EditableTodoSubTask) -> Unit),
    val added: Boolean = false,
    var edited: Boolean = false
) : ItemModel {
    override val id = task.id

    fun toggleDone(checked: Boolean) {
        task.done = if (checked) msFrom() else DoneState.ONGOING
        edited = true
    }

    fun postTitle(title: String) {
        task.title = title
        edited = true
    }

    fun onKey(v: View, actionId: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN &&
            actionId == IME_ACTION_PREVIOUS
        ) {
            requestRemove.invoke(this)
            return true
        }
        return false
    }
}