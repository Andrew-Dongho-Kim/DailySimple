package com.dd.android.dailysimple.schedule

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.databinding.ActivityMainBinding
import com.dd.android.dailysimple.databinding.ScheduleCardItemBinding
import com.dd.android.dailysimple.schedule.detail.ScheduleDetailActivity
import java.lang.ref.WeakReference

class ScheduleActivity : AppCompatActivity() {

    private lateinit var bind: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )

        with(bind.scheduleList) {
            layoutManager = LinearLayoutManager(this@ScheduleActivity)
            adapter =
                ScheduleCardAdapter().apply {
                    onItemClickListener = ScheduleCardItemClickListener(this@ScheduleActivity)
                }

            addItemDecoration(
                ScheduleCardItemDecoration(
                    this@ScheduleActivity
                )
            )
        }
    }

    private class ScheduleCardItemClickListener(activity: Activity) : View.OnClickListener {
        private val activityRef = WeakReference(activity)
        override fun onClick(view: View) {
            activityRef.get()?.let {
                it.startActivity(
                    Intent(it, ScheduleDetailActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                )
            }
        }
    }

    private class ScheduleCardItemDecoration(
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

    private class ScheduleCardItemViewHolder(
        val bind: ScheduleCardItemBinding
    ) : RecyclerView.ViewHolder(bind.root) {
    }

    private class ScheduleCardAdapter : RecyclerView.Adapter<ScheduleCardItemViewHolder>() {

        var onItemClickListener: View.OnClickListener? = null

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ) =
            ScheduleCardItemViewHolder(
                ScheduleCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
}
