package com.dd.android.dailysimple.daily

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.dd.android.dailysimple.BR
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.di.appDb
import com.dd.android.dailysimple.common.utils.DateUtils
import com.dd.android.dailysimple.common.widget.recycler.ViewHolder2
import com.dd.android.dailysimple.db.data.CheckStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


private const val TAG = "CheckStatus"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)
private inline fun logE(crossinline message: () -> String) = Logger.e(TAG, message)

@BindingAdapter("checkStatus")
fun applyCheckStatus(imageView: ImageView, checkedCount: Long) {
    imageView.setImageResource(
        if (checkedCount > 0) {
            R.drawable.checked_oval_stroke
        } else {
            R.drawable.unchecked_oval_stroke
        }
    )
}

class CheckStatusAdapter(
    private val lifecycleOwner: LifecycleOwner
) : PagedListAdapter<CheckStatus, CheckStatusViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CheckStatusViewHolder(parent).also {
            it.bind.lifecycleOwner = lifecycleOwner
        }

    override fun onBindViewHolder(holder: CheckStatusViewHolder, position: Int) {
        holder.bind.setVariable(holder.variableId, getItem(position))
        holder.bind.root.setTag(CHECK_STATUS_MODEL, getItem(position))
        holder.bind.root.setOnClickListener(onItemClick)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)!!.id
    }

    companion object {
        private const val CHECK_STATUS_MODEL = R.id.itemModel
        private val onItemClick = View.OnClickListener { view ->
            val status = (view.getTag(CHECK_STATUS_MODEL) as CheckStatus)

            GlobalScope.launch(Dispatchers.Default) {
                CheckStatus(
                    status.date,
                    status.habitId,
                    if (status.checkedCount == 0) 1 else 0
                ).also {
                    logD { "Inserted : ${Date(status.date)}, Id:${status.habitId}, Checked:${it.checkedCount}" }

                    val dao = appDb().checkStatusDao()
                    if (it.checkedCount > 0) dao.insert(it) else dao.delete(it)
                }
            }
        }

        private val diffCallback = object : DiffUtil.ItemCallback<CheckStatus>() {
            override fun areItemsTheSame(
                oldItem: CheckStatus,
                newItem: CheckStatus
            ): Boolean =
                oldItem.date == newItem.date && oldItem.habitId == newItem.habitId

            override fun areContentsTheSame(
                oldItem: CheckStatus,
                newItem: CheckStatus
            ): Boolean =
                oldItem == newItem
        }
    }
}

class CheckStatusViewHolder(parent: ViewGroup) :
    ViewHolder2<ViewDataBinding, CheckStatus>(
        parent,
        R.layout.daily_habit_check_state_item,
        BR.checkStatus
    )


data class Key(
    val date: Long,
    val index: Int
)

class CheckStatusDataSource(
    private val habitId: Long,
    private var checkedList: List<CheckStatus>
) : PageKeyedDataSource<Key, CheckStatus>() {

    private val pageSize = 7

    override fun loadBefore(params: LoadParams<Key>, callback: LoadCallback<Key, CheckStatus>) {
    }

    override fun loadInitial(
        params: LoadInitialParams<Key>,
        callback: LoadInitialCallback<Key, CheckStatus>
    ) {
        val cal = DateUtils.calendarDateOnly()
        val list = mutableListOf<CheckStatus>()

        val start = 0
        val last = loadFrom(cal, start, list)

        callback.onResult(
            list, null,
            Key(cal.timeInMillis, last)
        )
    }

    override fun loadAfter(params: LoadParams<Key>, callback: LoadCallback<Key, CheckStatus>) {
        val cal = Calendar.getInstance()
        cal.time = Date(params.key.date)

        val list = mutableListOf<CheckStatus>()

        val start = params.key.index
        val last = loadFrom(cal, start, list)

        callback.onResult(list, Key(cal.timeInMillis, last))
    }

    private fun loadFrom(cal: Calendar, start: Int, list: MutableList<CheckStatus>): Int {
        var curr = cal.timeInMillis
        var index = start
        for (i in 0 until pageSize) {
            if (index < checkedList.size && curr == checkedList[index].date) {
                list.add(checkedList[index])
                ++index
            } else {
                list.add(
                    CheckStatus(
                        curr,
                        habitId,
                        0
                    )
                )
            }
            cal.add(Calendar.DATE, -1)
            curr = cal.timeInMillis
        }
        return index
    }
}
