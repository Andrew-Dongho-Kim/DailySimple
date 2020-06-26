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
import com.dd.android.dailysimple.HomeFragmentDirections
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.BaseFragment
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.common.OnDateChangedListener
import com.dd.android.dailysimple.common.utils.DateUtils.msDateFrom
import com.dd.android.dailysimple.common.widget.adjustBigScreenWidth
import com.dd.android.dailysimple.daily.simplecalendar.SelectedDateInfo
import com.dd.android.dailysimple.daily.simplecalendar.SimpleCalendarController
import com.dd.android.dailysimple.daily.simplecalendar.SimpleCalendarViewModel
import com.dd.android.dailysimple.daily.viewmodel.HabitViewModel
import com.dd.android.dailysimple.daily.viewmodel.ScheduleViewModel
import com.dd.android.dailysimple.daily.viewmodel.TodoViewModel
import com.dd.android.dailysimple.databinding.FragmentDailyBinding
import com.dd.android.dailysimple.google.GoogleAccountViewModel
import com.dd.android.dailysimple.maker.BottomTaskCreator
import com.dd.android.dailysimple.maker.FabViewModel
import com.dd.android.dailysimple.plan.ScheduleCardItemDecoration
import kotlin.reflect.KClass

private const val TAG = "DailyMain"

private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class DailyFragment : BaseFragment<FragmentDailyBinding>(), OnDateChangedListener {
    /**
     * View Models
     */
    private val viewModelStoreOwner by lazy { this }
    private lateinit var fabVm: FabViewModel
    private lateinit var todoVm: TodoViewModel
    private lateinit var habitVm: HabitViewModel
    private lateinit var scheduleVm: ScheduleViewModel
    private lateinit var simpleCalendarVm: SimpleCalendarViewModel

    private lateinit var calendarController: SimpleCalendarController
    private var hasCalendarPermission = false

    override val layout: Int = R.layout.fragment_daily

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setStatusBarColor(R.color.basic_common_background)
        setUpCalendarPermission()
        setUpViewModel()
        setUpObserver()
        setUpSimpleCalendar()
        setUpContent()
        BottomTaskCreator(
            requireContext(),
            bind.fabLayout,
            fabVm, habitVm, todoVm, scheduleVm,
            viewLifecycleOwner,
            navController
        )
        setHasOptionsMenu(true)
    }

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

    private fun setUpObserver() {
        dateChangedObserver.addOnDateChangedListener(this)
    }

    private fun setUpViewModel() {
        fabVm = ViewModelProvider(activity).get(FabViewModel::class.java)
        todoVm = viewModel(TodoViewModel::class)
        habitVm = viewModel(HabitViewModel::class)
        scheduleVm = viewModel(ScheduleViewModel::class)
        simpleCalendarVm = viewModel(SimpleCalendarViewModel::class)

        bind.selectedDateInfo = SelectedDateInfo(simpleCalendarVm.selectedDate)
        bind.accountVm = activity.viewModels<GoogleAccountViewModel>().value
        bind.fabVm = fabVm
    }

    private fun <T : ViewModel> viewModel(kclass: KClass<T>) =
        ViewModelProvider(viewModelStoreOwner).get(kclass.java)

    private fun setUpSimpleCalendar() {
        bind.customToolbar.ymText.setOnClickListener {
            navController.navigate(
                HomeFragmentDirections.homeToDailyCalendar()
            )
        }
        bind.customToolbar.collapsibleToolbar.outlineProvider = ViewOutlineProvider.BACKGROUND
        bind.customToolbar.collapsibleToolbar.clipToOutline = true

        calendarController =
            SimpleCalendarController(
                bind.customToolbar.calendar, viewLifecycleOwner, viewModelStoreOwner
            ) {
                scheduleVm.selectedDate.postValue(it)
                habitVm.selectedDate.postValue(it)
                todoVm.selectedDate.postValue(it)
            }
    }

    private fun setUpContent() {
        val adapter = DailyAdapter(viewLifecycleOwner, viewModelStoreOwner, navController)
            .apply {
                setHasStableIds(true)
            }

        with(bind.recycler) {
            layoutManager = LinearLayoutManager(activity)
            itemAnimator = DefaultItemAnimator()
            this.adapter = adapter
            setUpCache()

            //addOnScrollListener(BottomBarScroll(requireActivity().findViewById(R.id.bottom_navigation_bar)))
            addItemDecoration(ScheduleCardItemDecoration(activity))
//            addItemDecoration(
//                StickyHeaderItemDecoration(
//                    this
//                ) { pos -> adapter.getItemViewType(pos) < 0 }
//            )
            setUpCache()
            adjustBigScreenWidth()
        }

        DailyItemModels(viewModelStoreOwner).data.observe(
            viewLifecycleOwner,
            Observer { list ->
                adapter.items.clear()
                adapter.items.addAll(list)
                adapter.notifyDataSetChanged()
            })

//        ItemTouchHelper(
//            DailyItemTouchAction(activity, adapter, navController)
//        ).attachToRecyclerView(recycler)
    }

    override fun onDateChanged() {
        simpleCalendarVm.selectedDate.postValue(msDateFrom())
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


