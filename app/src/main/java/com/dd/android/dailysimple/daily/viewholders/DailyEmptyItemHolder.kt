package com.dd.android.dailysimple.daily.viewholders

import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.navigation.NavController
import com.dd.android.dailysimple.HomeFragmentDirections
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.recycler.ItemModel
import com.dd.android.dailysimple.common.recycler.ViewHolder2
import com.dd.android.dailysimple.daily.DailyConst.NO_ID
import com.dd.android.dailysimple.daily.DailyViewType
import com.dd.android.dailysimple.daily.DailyViewType.Companion.HABIT_ITEM
import com.dd.android.dailysimple.daily.DailyViewType.Companion.SCHEDULE_ITEM
import com.dd.android.dailysimple.daily.DailyViewType.Companion.TODO_ITEM
import com.dd.android.dailysimple.databinding.DailyEmptyItemBinding

class DailyEmptyItemHolder(parent: ViewGroup, private val navController: NavController) :
    ViewHolder2<DailyEmptyItemBinding, DailyEmptyItemModel>(
        parent,
        R.layout.daily_empty_item,
        BR.itemModel
    ) {
    init {
        itemClickListener = ::onClick
    }

    private fun onClick(view: View) {
        model?.let {
            when (it.type) {
                SCHEDULE_ITEM -> navController.navigate(
                    HomeFragmentDirections.homeToMakeAndEditTodo(NO_ID)
                )

                TODO_ITEM -> navController.navigate(
                    HomeFragmentDirections.homeToMakeAndEditTodo(NO_ID)
                )

                HABIT_ITEM -> navController.navigate(
                    HomeFragmentDirections.homeToMakeAndEditHabit(NO_ID)
                )
            }
        }
    }
}

data class DailyEmptyItemModel(
    override val id: Long,
    @DailyViewType val type: Int,
    val description: String
) : ItemModel