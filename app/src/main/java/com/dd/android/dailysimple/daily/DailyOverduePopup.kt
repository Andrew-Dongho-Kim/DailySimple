package com.dd.android.dailysimple.daily

import android.content.Context
import android.view.LayoutInflater
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.daily.viewmodel.TodoViewModel
import com.dd.android.dailysimple.databinding.DailyOverduePopupWindowBinding

class DailyOverduePopup(
    context: Context,
    viewModelStoreOwner: ViewModelStoreOwner,
    private val todoId: Long
) :
    PopupWindow(
        LayoutInflater.from(context).inflate(R.layout.daily_overdue_popup_window, null),
        context.resources.getDimensionPixelSize(R.dimen.daily_overdue_popup_width),
        context.resources.getDimensionPixelSize(R.dimen.daily_overdue_popup_height),
        true
    ) {

    private val toVm = ViewModelProvider(viewModelStoreOwner).get(TodoViewModel::class.java)

    private val bind = DataBindingUtil.bind<DailyOverduePopupWindowBinding>(contentView)!!

    init {
        bind.done.setOnClickListener { onDoneClick() }
        bind.edit.setOnClickListener { onEditClick() }
        bind.delete.setOnClickListener { onDeleteClick() }
    }


    private fun onDoneClick() {
        toVm.makeToDone(todoId)
        dismiss()
    }

    private fun onEditClick() {
        dismiss()
    }

    private fun onDeleteClick() {
        toVm.deleteTodo(todoId)
        dismiss()
    }
}