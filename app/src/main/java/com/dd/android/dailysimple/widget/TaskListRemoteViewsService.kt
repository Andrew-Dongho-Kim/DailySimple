package com.dd.android.dailysimple.widget

import android.app.Application
import android.content.Intent
import android.os.IBinder
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.widget.recycler.ItemModel
import com.dd.android.dailysimple.daily.DailyItemModels

// https://codelabs.developers.google.com/codelabs/advanced-android-training-widgets/index.html?index=..%2F..advanced-android-training#0
class TaskListRemoteViewsService : RemoteViewsService(), LifecycleOwner, ViewModelStoreOwner {

    private val lifecycleDispatcher = ServiceLifecycleDispatcher(this)
    private val viewModelStore = ViewModelStore()

    private val taskItemRemoteViewsFactory = TaskItemRemoteViewsFactory(application)
    private val itemModel = DailyItemModels(this)

    override fun onCreate() {
        lifecycleDispatcher.onServicePreSuperOnCreate()
        super.onCreate()

        itemModel.data.observe(this, Observer {
            taskItemRemoteViewsFactory.submitList(it)
        })
    }

    override fun onBind(intent: Intent?): IBinder? {
        lifecycleDispatcher.onServicePreSuperOnBind()
        return super.onBind(intent)
    }

    override fun onStart(intent: Intent?, startId: Int) {
        lifecycleDispatcher.onServicePreSuperOnStart()
        super.onStart(intent, startId)
    }

    override fun onDestroy() {
        lifecycleDispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    override fun getLifecycle(): Lifecycle = lifecycleDispatcher.lifecycle
    override fun getViewModelStore() = viewModelStore
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory = taskItemRemoteViewsFactory

}


private class TaskItemRemoteViewsFactory(private val app: Application) :
    RemoteViewsService.RemoteViewsFactory {

    private val taskItems = mutableListOf<ItemModel>()

    fun submitList(items:List<ItemModel>) {
        val result = DiffUtil.calculateDiff(DiffCallback())
        taskItems.clear()
    }

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
        return RemoteViews(app.packageName, R.layout.widget_item_todo).apply {

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

    private class DiffCallback : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            TODO("Not yet implemented")
        }

        override fun getOldListSize(): Int {
            TODO("Not yet implemented")
        }

        override fun getNewListSize(): Int {
            TODO("Not yet implemented")
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            TODO("Not yet implemented")
        }
    }
}