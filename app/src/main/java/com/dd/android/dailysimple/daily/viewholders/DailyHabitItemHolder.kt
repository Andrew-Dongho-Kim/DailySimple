package com.dd.android.dailysimple.daily.viewholders

import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import com.dd.android.dailysimple.HomeFragmentDirections
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.recycler.ItemModel
import com.dd.android.dailysimple.daily.CheckStatusAdapter
import com.dd.android.dailysimple.daily.viewmodel.HabitCheckStatusViewModel
import com.dd.android.dailysimple.databinding.DailyHabitItemBinding
import com.dd.android.dailysimple.db.data.CheckStatus
import com.dd.android.dailysimple.db.data.DailyHabit

private const val TAG = "DailyHabitItemHolder"

private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class DailyHabitItemHolder(
    parent: ViewGroup,
    private val lifecycleOwner: LifecycleOwner,
    viewModelStoreOwner: ViewModelStoreOwner,
    private val navController: NavController,
    sharedScrollStatus: SharedScrollStatus
) :
    SlaveScrollViewHolder<DailyHabitItemBinding, DailyHabit>(
        parent,
        R.layout.daily_habit_item,
        R.id.checked_recycler_view,
        BR.itemModel,
        sharedScrollStatus
    ) {

    private val adapter = CheckStatusAdapter(lifecycleOwner)

    private val checkedVm = ViewModelProvider(viewModelStoreOwner).get(
        HabitCheckStatusViewModel::class.java
    )

    init {
        recycler.adapter = adapter
        with(layoutManager) {
            orientation = RecyclerView.HORIZONTAL
            reverseLayout = true
        }
        bind.habitTitle.setOnClickListener(::onClick)
    }

    private val observerAdapter = Observer<PagedList<CheckStatus>> {
        logD { "Check status is submitted :${model?.id}" }
        adapter.submitList(it)
    }

    override fun bindTo(itemModel: ItemModel) {
        super.bindTo(itemModel)

        adapter.submitList(null)
        checkedVm.getCheckedStatusPagedList(lifecycleOwner, itemModel.id)
            .also {
                it.removeObservers(lifecycleOwner)
                it.observe(lifecycleOwner, observerAdapter)
            }
    }

    private fun onClick(view: View) {
        navController.navigate(
            HomeFragmentDirections.homeToDailyDetail(model!!.id)
        )
    }
}