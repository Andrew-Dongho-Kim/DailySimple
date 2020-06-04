package com.dd.android.dailysimple.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.dd.android.dailysimple.R

// https://codelabs.developers.google.com/codelabs/advanced-android-training-widgets/index.html?index=..%2F..advanced-android-training#0
class TaskListRemoteViewsService : RemoteViewsService() {

    class TaskItemRemoteViewsFactory(private val context: Context) : RemoteViewsFactory {
        override fun onCreate() {
        }

        override fun getLoadingView(): RemoteViews {
            TODO("Not yet implemented")
        }

        override fun getItemId(position: Int): Long {
            TODO("Not yet implemented")
        }

        override fun onDataSetChanged() {
        }

        override fun hasStableIds() = true

        override fun getViewAt(position: Int): RemoteViews {
            return RemoteViews(context.packageName, R.layout.app_widget_today_task_item).apply {

            }
        }

        override fun getCount(): Int {
            TODO("Not yet implemented")
        }

        override fun getViewTypeCount(): Int {
            TODO("Not yet implemented")
        }

        override fun onDestroy() {
            TODO("Not yet implemented")
        }
    }


    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return TaskItemRemoteViewsFactory(this)
    }
}