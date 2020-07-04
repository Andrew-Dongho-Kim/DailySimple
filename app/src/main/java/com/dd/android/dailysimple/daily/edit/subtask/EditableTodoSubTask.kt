package com.dd.android.dailysimple.daily.edit.subtask

import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo.IME_ACTION_PREVIOUS
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.dd.android.dailysimple.BR
import com.dd.android.dailysimple.common.utils.DateUtils.msFrom
import com.dd.android.dailysimple.common.widget.recycler.ItemModel
import com.dd.android.dailysimple.db.data.DoneState.Companion.ONGOING
import com.dd.android.dailysimple.db.data.DoneState.Companion.isDone
import com.dd.android.dailysimple.db.data.TodoSubTask

data class EditableTodoSubTask(
    val task: TodoSubTask,
    val position: Int,
    val added: Boolean = false,
    var edited: Boolean = false
) : BaseObservable(), ItemModel {

    override val id = task.id

    override val selected = MutableLiveData<Boolean>()

    // @formatter:off
    @get:Bindable val done get() = task.done
    @get:Bindable val doneMD get() = task.doneMD
    @get:Bindable val title get() = task.title
    // @formatter:on

    fun toggleDone(checked: Boolean) {
        if (checked) {
            if (!isDone(task.done)) task.done = msFrom()
        } else {
            task.done = ONGOING
        }
        edited = true
        notifyPropertyChanged(BR.done)
        notifyPropertyChanged(BR.doneMD)
    }

    fun postTitle(title: String) {
        task.title = title
        edited = true
        notifyPropertyChanged(BR.title)
    }

    fun onKey(v: View, actionId: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN &&
            actionId == IME_ACTION_PREVIOUS
        ) {
            return true
        }
        return false
    }
}