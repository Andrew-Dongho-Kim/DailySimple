package com.dd.android.dailysimple.schedule.daily

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dd.android.dailysimple.databinding.DailyScheduleCardItemBinding

class DailyScheduleAdapter : RecyclerView.Adapter<DailyScheduleItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DailyScheduleItemViewHolder(
            DailyScheduleCardItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )


    override fun onBindViewHolder(holder: DailyScheduleItemViewHolder, position: Int) {
    }

    override fun getItemCount() = 3
}


class DailyScheduleItemViewHolder(
    val bind: DailyScheduleCardItemBinding
) : RecyclerView.ViewHolder(bind.root) {

}