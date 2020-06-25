package com.dd.android.dailysimple.widget

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.provider.Settings
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.lifecycle.*
import com.dd.android.dailysimple.HomeActivity
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.utils.DateUtils.msDateFrom
import com.dd.android.dailysimple.common.widget.createRemoteViews
import com.dd.android.dailysimple.common.widget.recycler.ItemModel
import com.dd.android.dailysimple.common.widget.setViewBackground
import com.dd.android.dailysimple.daily.DailyItemModels
import com.dd.android.dailysimple.daily.DailyViewType.Companion.AUTHORITY_ITEM
import com.dd.android.dailysimple.daily.DailyViewType.Companion.EMPTY_ITEM
import com.dd.android.dailysimple.daily.DailyViewType.Companion.HABIT_ITEM
import com.dd.android.dailysimple.daily.DailyViewType.Companion.SCHEDULE_ITEM
import com.dd.android.dailysimple.daily.DailyViewType.Companion.SIMPLE_HEADER
import com.dd.android.dailysimple.daily.DailyViewType.Companion.TODO_ITEM
import com.dd.android.dailysimple.daily.IdMap
import com.dd.android.dailysimple.daily.viewholders.DailyAuthorityItem
import com.dd.android.dailysimple.daily.viewholders.DailyEmptyItem
import com.dd.android.dailysimple.daily.viewholders.DailySimpleHeaderItem
import com.dd.android.dailysimple.daily.viewmodel.HabitViewModel
import com.dd.android.dailysimple.daily.viewmodel.ScheduleViewModel
import com.dd.android.dailysimple.daily.viewmodel.TodoViewModel
import com.dd.android.dailysimple.db.data.DailyHabitWithCheckStatus
import com.dd.android.dailysimple.db.data.DailySchedule
import com.dd.android.dailysimple.db.data.DailyTodo
import com.dd.android.dailysimple.db.data.DoneState.Companion.isDone
import com.dd.android.dailysimple.setting.SettingManager
import kotlinx.coroutines.*


private const val TAG = "TodoListRemoteViews"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class TaskListRemoteViewsService : RemoteViewsService(), LifecycleOwner {

    private val lifecycleDispatcher by lazy { ServiceLifecycleDispatcher(this) }

    override fun onCreate() {
        lifecycleDispatcher.onServicePreSuperOnCreate()
        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder? {
        lifecycleDispatcher.onServicePreSuperOnBind()
        return super.onBind(intent)
    }

    @Suppress("deprecation")
    override fun onStart(intent: Intent?, startId: Int) {
        lifecycleDispatcher.onServicePreSuperOnStart()
        super.onStart(intent, startId)
    }

    override fun onDestroy() {
        lifecycleDispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    override fun getLifecycle(): Lifecycle = lifecycleDispatcher.lifecycle

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory =
        TaskItemRemoteViewsFactory(application)

}


private class TaskItemRemoteViewsFactory(
    private val app: Application
) : RemoteViewsService.RemoteViewsFactory, ViewModelStoreOwner,
    HasDefaultViewModelProviderFactory, Observer<List<ItemModel>> {

    private val settingManager by lazy { SettingManager(app) }

    private val viewModelStore = ViewModelStore()
    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val itemModel by lazy { DailyItemModels(this) }
    private val items = mutableListOf<ItemModel>()


    override fun onCreate() {
        logD { "TaskItemRemoveViewsFactory was created:$this" }
        itemModel.data.observeForever(this)
    }

    override fun getLoadingView(): RemoteViews {
        return RemoteViews(app.packageName, R.layout.widget_todo_item)
    }

    override fun hasStableIds() = true

    override fun getItemId(position: Int) =
        items[position].run { (IdMap[javaClass] ?: error("unknown : $this")).idBase + id }

    override fun getViewAt(position: Int): RemoteViews {
        return when (getViewType(position)) {
            SIMPLE_HEADER -> createHeaderItem(items[position] as DailySimpleHeaderItem)
            EMPTY_ITEM -> createEmptyItem(items[position] as DailyEmptyItem)
            AUTHORITY_ITEM -> createAuthorityItem(items[position] as DailyAuthorityItem)
            SCHEDULE_ITEM -> createScheduleItem(items[position] as DailySchedule)
            TODO_ITEM -> createTodoItem(items[position] as DailyTodo)
            HABIT_ITEM -> createHabitItem(items[position] as DailyHabitWithCheckStatus)
            else -> RemoteViews(app.packageName, R.layout.widget_todo_item)
            //throw IllegalStateException("Unknown View-type :${getViewType(position)}")
        }
    }

    fun createHeaderItem(headerItem: DailySimpleHeaderItem) =
        createRemoteViews(R.layout.widget_header_item).apply {
            setTextViewText(R.id.header_text, headerItem.headerTitle)
        }

    fun createEmptyItem(emptyItem: DailyEmptyItem) =
        createRemoteViews(R.layout.widget_empty_item).apply {
            setTextViewText(R.id.description_text, emptyItem.description)
        }


    fun createAuthorityItem(authorityItem: DailyAuthorityItem) =
        createRemoteViews(R.layout.widget_authority_item).apply {
            setOnClickFillInIntent(
                R.id.root_view,
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", app.packageName, null)
                )
            )
            setTextViewText(R.id.description_text, authorityItem.description)
        }


    fun createScheduleItem(scheduleItem: DailySchedule): RemoteViews {
        return createRemoteViews(R.layout.widget_schedule_item).apply {
            setTextViewText(R.id.title, scheduleItem.title)
            setTextViewText(R.id.begin, scheduleItem.beginTime)
            setTextViewText(R.id.end, scheduleItem.endTime)
            setViewVisibility(R.id.end, if (scheduleItem.isAllDay) View.INVISIBLE else View.VISIBLE)
            setViewBackground(R.id.color, scheduleItem.color)
            setOnClickFillInIntent(R.id.root_view, Intent(app, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }

    fun createTodoItem(todoItem: DailyTodo): RemoteViews {
        return createRemoteViews(R.layout.widget_todo_item).apply {
            setTextViewText(R.id.title, todoItem.title)
            setViewBackground(R.id.color, todoItem.color)
            setImageViewResource(
                R.id.check,
                if (isDone(todoItem.done)) {
                    R.drawable.checked_oval_stroke
                } else {
                    R.drawable.unchecked_oval_stroke
                }
            )
        }
    }

    fun createHabitItem(habitItem: DailyHabitWithCheckStatus): RemoteViews {
        return RemoteViews(app.packageName, R.layout.widget_todo_item).apply {
            setTextViewText(R.id.title, habitItem.habit.title)
            setViewBackground(R.id.color, habitItem.habit.color)
            setImageViewResource(
                R.id.check,
                if (habitItem.done.value == true) {
                    R.drawable.checked_oval_stroke
                } else {
                    R.drawable.unchecked_oval_stroke
                }
            )
        }
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getViewTypeCount(): Int {
        return 5
    }

    override fun onDestroy() {
        mainScope.launch {
            itemModel.data.removeObserver(this@TaskItemRemoteViewsFactory)
            cancel()
        }
    }

    override fun getViewModelStore() = viewModelStore

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when (modelClass) {
                    ScheduleViewModel::class.java -> ScheduleViewModel(app)
                    HabitViewModel::class.java -> HabitViewModel(app)
                    else -> TodoViewModel(app)
                } as T
            }
        }
    }

    override fun onDataSetChanged() {
        logD { "onDataSetChanged()" }
        itemModel.postDate(
            settingManager.getLong(
                WidgetConst.SETTING_KEY_WIDGET_SELECTED_DATE,
                msDateFrom()
            )
        )
    }

    override fun onChanged(list: List<ItemModel>) {
        items.clear()
        items.addAll(list)

        val appWidgetManager = AppWidgetManager.getInstance(app)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(
                app,
                WidgetProvider::class.java
            )
        )
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.task_list)
        logD { "ItemModel was changed:${list.size}" }
    }

    fun getViewType(position: Int) =
        items[position].run { (IdMap[javaClass] ?: error("unknown : $this")).viewType }

}