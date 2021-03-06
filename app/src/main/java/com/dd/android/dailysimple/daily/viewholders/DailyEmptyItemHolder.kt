package com.dd.android.dailysimple.daily.viewholders

import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.dd.android.dailysimple.HomeFragmentDirections.Companion.homeToMakeAndEdit
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.widget.recycler.ItemModel
import com.dd.android.dailysimple.common.widget.recycler.ViewHolder2
import com.dd.android.dailysimple.daily.AppConst.NO_ID
import com.dd.android.dailysimple.daily.DailyViewType
import com.dd.android.dailysimple.daily.DailyViewType.Companion.HABIT_ITEM
import com.dd.android.dailysimple.daily.DailyViewType.Companion.SCHEDULE_ITEM
import com.dd.android.dailysimple.daily.DailyViewType.Companion.TODO_ITEM
import com.dd.android.dailysimple.daily.edit.EditType
import com.dd.android.dailysimple.databinding.DailyEmptyItemBinding

class DailyEmptyItemHolder(parent: ViewGroup, private val navController: NavController) :
    ViewHolder2<DailyEmptyItemBinding, DailyEmptyItem>(
        parent,
        R.layout.daily_empty_item,
        BR.itemModel,
        supportActionMode = false
    ) {
    init {
        itemClickListener = { _ -> onClick() }
    }

    private fun onClick() {
        model?.let {
            when (it.type) {
                SCHEDULE_ITEM -> navController.navigate(homeToMakeAndEdit(NO_ID, EditType.SCHEDULE))
                TODO_ITEM -> navController.navigate(homeToMakeAndEdit(NO_ID, EditType.TODO))
                HABIT_ITEM -> navController.navigate(homeToMakeAndEdit(NO_ID, EditType.HABIT))
            }
        }
    }
}

data class DailyEmptyItem(
    override val id: Long,
    @DailyViewType val type: Int,
    val description: String
) : ItemModel {

    override val selected = MutableLiveData<Boolean>()
}