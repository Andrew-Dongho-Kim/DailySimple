package com.dd.android.dailysimple.daily

import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
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
import com.dd.android.dailysimple.common.utils.DateUtils.msDateOnlyFrom
import com.dd.android.dailysimple.common.widget.recycler.StickyHeaderItemDecoration
import com.dd.android.dailysimple.daily.simplecalendar.SelectedDateInfo
import com.dd.android.dailysimple.daily.simplecalendar.SimpleCalendarController
import com.dd.android.dailysimple.daily.simplecalendar.SimpleCalendarViewModel
import com.dd.android.dailysimple.daily.viewmodel.HabitViewModel
import com.dd.android.dailysimple.daily.viewmodel.ScheduleViewModel
import com.dd.android.dailysimple.daily.viewmodel.TodoViewModel
import com.dd.android.dailysimple.databinding.FragmentDailyBinding
import com.dd.android.dailysimple.google.GoogleAccountViewModel
import com.dd.android.dailysimple.maker.BottomJobMaker
import com.dd.android.dailysimple.maker.FabViewModel
import com.dd.android.dailysimple.plan.ScheduleCardItemDecoration
import kotlin.reflect.KClass

private const val TAG = "DailyFragment"

private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class DailyFragment : BaseFragment<FragmentDailyBinding>(), OnDateChangedListener {

    private lateinit var fabVm: FabViewModel
    private lateinit var todoVm: TodoViewModel
    private lateinit var habitVm: HabitViewModel
    private lateinit var scheduleVm: ScheduleViewModel
    private lateinit var simpleCalendarVm: SimpleCalendarViewModel

    private lateinit var calendarController: SimpleCalendarController

    private val viewModelStoreOwner by lazy { this }

    override val layout: Int = R.layout.fragment_daily

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setStatusBarColor(R.color.basic_common_background)
        setUpViewModel()
        setUpObserver()
        setUpSimpleCalendar()
        setUpContent()
        BottomJobMaker(
            requireContext(),
            bind.fabLayout,
            fabVm, habitVm, todoVm, scheduleVm,
            viewLifecycleOwner,
            navController
        )
        setHasOptionsMenu(true)
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

            //addOnScrollListener(BottomBarScroll(requireActivity().findViewById(R.id.bottom_navigation_bar)))
            addItemDecoration(ScheduleCardItemDecoration(activity))
            addItemDecoration(
                StickyHeaderItemDecoration(
                    this
                ) { pos -> adapter.getItemViewType(pos) < 0 }
            )
            setUpCache()
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
        simpleCalendarVm.selectedDate.postValue(msDateOnlyFrom())
        calendarController.invalidate()
    }

    private fun setUpObserver() {
        dateChangedObserver.addOnDateChangedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.daily_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}


