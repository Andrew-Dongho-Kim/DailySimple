package com.dd.android.dailysimple.daily.viewholders

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.widget.recycler.ViewHolder2
import com.dd.android.dailysimple.daily.DailyExpandableItem
import com.dd.android.dailysimple.daily.viewholders.DailyTodoGroup.Companion.iconDegree
import com.dd.android.dailysimple.databinding.DailyTodoGroupItemBinding
import com.dd.android.dailysimple.db.data.DailyTodo

class DailyTodoGroupHolder(parent: ViewGroup) :
    ViewHolder2<DailyTodoGroupItemBinding, DailyTodoGroup>(
        parent,
        R.layout.daily_todo_group_item,
        BR.itemModel
    ) {
    init {
        itemClickListener = ::onClick
    }

    private fun onClick(view: View) {
        model?.let {
            val toggled = !(it.isExpanded.value ?: false)
            it.isAnimating.postValue(true)
            it.isExpanded.postValue(toggled)
            bind.expandIcon.animate().cancel()
            bind.expandIcon.animate().rotation(iconDegree(toggled)).setListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        it.isAnimating.postValue(false)
                    }
                }
            )
        }
    }
}

data class DailyTodoGroup(
    override val id: Long,
    val message: String,
    private val expanded: MutableLiveData<Boolean>,
    private val todoList: List<DailyTodo>
) : DailyExpandableItem(todoList, expanded) {

    val isAnimating = liveData {
        emit(false)
    } as MutableLiveData<Boolean>

    val iconDegree = Transformations.map(isExpanded) { isExpanded ->
        iconDegree(isExpanded)
    }

    companion object {
        private const val ROT_EXPANDED = 90f

        private const val ROT_COLLAPSED = 0f

        fun iconDegree(isExpanded: Boolean) =
            if (isExpanded) ROT_EXPANDED else ROT_COLLAPSED
    }
}
