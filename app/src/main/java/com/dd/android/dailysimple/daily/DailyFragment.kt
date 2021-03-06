package com.dd.android.dailysimple.daily

import android.Manifest.permission.READ_CALENDAR
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewOutlineProvider
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.dd.android.dailysimple.HomeFragmentDirections.Companion.homeToDailyCalendar
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.BaseFragment
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.OnDateChangedListener
import com.dd.android.dailysimple.common.extensions.adjustBigScreenWidth
import com.dd.android.dailysimple.common.utils.DateUtils.msDateFrom
import com.dd.android.dailysimple.common.widget.recycler.ItemModel
import com.dd.android.dailysimple.common.widget.recycler.RecyclerViewActionMode
import com.dd.android.dailysimple.common.widget.recycler.StickyHeaderItemDecoration
import com.dd.android.dailysimple.daily.scroll.BottomBarScroll
import com.dd.android.dailysimple.daily.simplecalendar.SelectedDateInfo
import com.dd.android.dailysimple.daily.simplecalendar.SimpleCalendarHelper
import com.dd.android.dailysimple.daily.simplecalendar.SimpleCalendarViewModel
import com.dd.android.dailysimple.daily.viewmodel.HabitViewModel
import com.dd.android.dailysimple.daily.viewmodel.ScheduleViewModel
import com.dd.android.dailysimple.daily.viewmodel.TodoViewModel
import com.dd.android.dailysimple.databinding.FragmentDailyBinding
import com.dd.android.dailysimple.google.GoogleAccountViewModel
import com.dd.android.dailysimple.maker.BottomTaskCreator
import com.dd.android.dailysimple.maker.FabViewModel
import com.dd.android.dailysimple.plan.ScheduleCardItemDecoration
import java.util.*
import kotlin.reflect.KClass

private const val TAG = "DailyMain"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

/**
 * @see com.dd.android.dailysimple.R.id.daily_fragment
 */
private const val ARG_DATE = "date"

class DailyFragment : BaseFragment<FragmentDailyBinding>(), RecyclerViewActionMode.Callback,
    OnDateChangedListener {
    /**
     * View Models & ViewModel Store Owner
     */
    private val viewModelStoreOwner by lazy { requireActivity() }
    private lateinit var fabVm: FabViewModel
    private lateinit var todoVm: TodoViewModel
    private lateinit var habitVm: HabitViewModel
    private lateinit var scheduleVm: ScheduleViewModel
    private lateinit var simpleCalendarVm: SimpleCalendarViewModel
    private val itemModel by lazy { DailyItemModels(viewModelStoreOwner) }

    private lateinit var calendarController: SimpleCalendarHelper
    private var hasCalendarPermission = false

    private val toolbar by lazy { bind.customToolbar.toolbar }

    override val layout: Int = R.layout.fragment_daily

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setStatusBarColor(R.color.basic_common_background)
        setUpToolbar()
        setUpCalendarPermission()
        setUpViewModel()
        setUpObserver()

        setUpCalendarHeader()
        setUpBottomTaskCreator()
        setUpContent()
        setUpArguments()
    }

    private fun <T : ViewModel> viewModel(kclass: KClass<T>) =
        ViewModelProvider(viewModelStoreOwner).get(kclass.java)

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        with(getSupportActionBar()!!) {
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowTitleEnabled(false)
            setDisplayShowHomeEnabled(false)
            setDisplayShowCustomEnabled(false)
        }
        setHasOptionsMenu(true)
    }

    /**
     * Set up calendar permission to get schedule information for the user
     */
    private fun setUpCalendarPermission() {
        registerForActivityResult(RequestPermission()) { granted ->
            if (granted) {
                scheduleVm.refresh()
                hasCalendarPermission = true
            }
        }.launch(READ_CALENDAR)
        hasCalendarPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            READ_CALENDAR
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setUpArguments() {
        val date = arguments?.getLong(ARG_DATE, 0L) ?: 0L

        if (date != 0L) {
            arguments?.putLong(ARG_DATE, 0L)
            simpleCalendarVm.selectedDate.value = date
            logD { "setUpArguments date : ${Date(date)}" }
        }
    }

    private fun setUpObserver() {
        dateChangedObserver.addOnDateChangedListener(this)
    }

    private fun setUpViewModel() {
        fabVm = viewModel(FabViewModel::class)
        todoVm = viewModel(TodoViewModel::class)
        habitVm = viewModel(HabitViewModel::class)
        scheduleVm = viewModel(ScheduleViewModel::class)
        simpleCalendarVm = viewModel(SimpleCalendarViewModel::class)

        bind.selectedDateInfo = SelectedDateInfo(simpleCalendarVm.selectedDate)
        bind.accountVm = activity.viewModels<GoogleAccountViewModel>().value
        bind.fabVm = fabVm
    }

    private fun setUpCalendarHeader() {
        with(bind.customToolbar) {
            ymText.setOnClickListener { navController.navigate(homeToDailyCalendar()) }
            collapsibleToolbar.outlineProvider = ViewOutlineProvider.BACKGROUND
            collapsibleToolbar.clipToOutline = true
        }

        calendarController =
            SimpleCalendarHelper(
                bind.customToolbar.calendar, viewLifecycleOwner, viewModelStoreOwner
            ) {
                itemModel.postDate(it)
            }
    }

    private fun setUpBottomTaskCreator() {
        BottomTaskCreator(
            requireContext(),
            bind.fabLayout,
            fabVm, habitVm, todoVm, scheduleVm,
            viewLifecycleOwner,
            navController
        )
    }

    private fun setUpContent() {
        val adapter =
            DailyAdapter(activity, viewLifecycleOwner, viewModelStoreOwner, navController, this)
                .apply {
                    setHasStableIds(true)
                }

        itemModel.data.observe(viewLifecycleOwner, Observer { list ->
            adapter.submitList(list)
        })

        with(bind.recycler) {
            layoutManager = LinearLayoutManager(activity)
            itemAnimator = DefaultItemAnimator()
            this.adapter = adapter
            setUpCache()

            addOnScrollListener(BottomBarScroll(bind.fabLayout.root))
            addItemDecoration(ScheduleCardItemDecoration(activity))
            addItemDecoration(
                StickyHeaderItemDecoration(
                    this
                ) { pos -> adapter.getItemViewType(pos) < 0 }
            )
            setUpCache()
            adjustBigScreenWidth()
        }


        //        ItemTouchHelper(
//            DailyItemTouchAction(activity, adapter, navController)
//        ).attachToRecyclerView(recycler)
    }


    override fun onCreateActionMode(
        mode: RecyclerViewActionMode,
        menu: Menu,
        selected: Set<ItemModel>
    ): Boolean {
        toolbar.menu.clear()
        mode.titleOptionalHint = false
        mode.menuInflater?.inflate(R.menu.daily_action_menu, toolbar.menu)
        return true
    }

    override fun onDestroyActionMode(mode: RecyclerViewActionMode) {
        toolbar.menu.clear()
        mode.menuInflater?.inflate(R.menu.daily_menu, toolbar.menu)
    }

    override fun onDateChanged() {
        val newDate = msDateFrom()

        simpleCalendarVm.selectedDate.value = newDate
        calendarController.invalidate()
    }

    override fun onStart() {
        super.onStart()
        if (!hasCalendarPermission) {
            val hasPermission = ContextCompat.checkSelfPermission(
                requireContext(),
                READ_CALENDAR
            ) == PackageManager.PERMISSION_GRANTED
            if (hasCalendarPermission != hasPermission) {
                logD { "Calendar permission is grated : $hasPermission" }

                hasCalendarPermission = hasPermission
                scheduleVm.refresh()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.daily_menu, menu)
    }
}


