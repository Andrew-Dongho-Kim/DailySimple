package com.dd.android.dailysimple.plan

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dd.android.dailysimple.HomeFragmentDirections
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.recycler.ViewHolder2

class GroupScheduleAdapter : RecyclerView.Adapter<ViewHolder2>() {

    var onItemClickListener: View.OnClickListener? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ViewHolder2(parent, R.layout.plan_card_item, viewType)

    override fun onBindViewHolder(holder: ViewHolder2, position: Int) {
        holder.itemClickListener = onItemClickListener
    }

    override fun getItemCount(): Int {
        return 10
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