package com.dd.android.dailysimple.schedule.group

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.databinding.GroupScheduleCardItemBinding
import com.dd.android.dailysimple.schedule.HomeViewPagerFragmentDirections

class GroupScheduleAdapter : RecyclerView.Adapter<ScheduleCardItemViewHolder>() {

    var onItemClickListener: View.OnClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) =
        ScheduleCardItemViewHolder(
            GroupScheduleCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                .apply {
                    root.setOnClickListener(onItemClickListener)
                }
        )

    override fun onBindViewHolder(holder: ScheduleCardItemViewHolder, position: Int) {
        holder.bind
    }

    override fun getItemCount(): Int {
        return 10
    }
}


class ScheduleCardItemClickListener : View.OnClickListener {
    override fun onClick(view: View) {
        view.findNavController().navigate(
            HomeViewPagerFragmentDirections.homeToGroupScheduleDetailFragment(0)
        )
    }
}

class ScheduleCardItemDecoration(
    private val context: Context
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = context.resources.getDimensionPixelSize(R.dimen.list_item_margin_vertical)
    }
}

class ScheduleCardItemViewHolder(
    val bind: GroupScheduleCardItemBinding
) : RecyclerView.ViewHolder(bind.root) {
}