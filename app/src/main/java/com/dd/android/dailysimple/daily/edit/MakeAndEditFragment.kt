package com.dd.android.dailysimple.daily.edit

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import com.dd.android.dailysimple.R
import com.dd.android.dailysimple.common.BaseFragment
import com.dd.android.dailysimple.common.di.systemLocale
import com.dd.android.dailysimple.common.utils.DateUtils.msDateFrom
import com.dd.android.dailysimple.common.utils.DateUtils.strYmdToLong
import com.dd.android.dailysimple.common.utils.DateUtils.toTime
import com.dd.android.dailysimple.common.utils.DateUtils.toYMD
import com.dd.android.dailysimple.common.extensions.setUnderlineText
import com.dd.android.dailysimple.common.widget.TimePickerDialogFragment
import com.dd.android.dailysimple.common.extensions.adjustBigScreenWidth
import com.dd.android.dailysimple.daily.AppConst.NO_ID
import com.dd.android.dailysimple.databinding.FragmentMakeAndEditBinding
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener

/**
 * @see com.dd.android.dailysimple.R.id.make_and_edit_fragment
 */
private const val ARG_TYPE = "type"
private const val ARG_ID = "id"

class MakeAndEditFragment : BaseFragment<FragmentMakeAndEditBinding>() {

    private lateinit var editor: Editable

    private val viewModelStoreOwner by lazy { requireActivity() }

    override val layout: Int = R.layout.fragment_make_and_edit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpContent()
        setUpEditor()
        setUpBind()
        setStatusBarColor(R.color.basic_view_background)
    }

    private fun setUpContent() {
        bind.collapsibleToolbar.adjustBigScreenWidth()
        bind.scrollableContent.adjustBigScreenWidth()
    }

    private fun setUpEditor() {
        editor = when (requireArguments().get(ARG_TYPE) as Int) {
            EditType.SCHEDULE -> EditorSchedule(
                requireContext(),
                bind,
                viewLifecycleOwner,
                viewModelStoreOwner
            )
            EditType.TODO -> EditorTodo(
                requireContext(),
                bind,
                viewLifecycleOwner,
                viewModelStoreOwner
            )
            EditType.HABIT -> EditorHabit(
                requireContext(),
                bind,
                viewLifecycleOwner,
                viewModelStoreOwner
            )
            else -> throw IllegalArgumentException("Illegal EditType")
        }
    }

    private fun setUpBind() {
        val id = arguments?.getLong(ARG_ID) ?: NO_ID

        editor.bind(id)
        bind.ui = this
    }

    fun onColorPickerClick(view: View) {
        ColorPickerDialog.newBuilder()
            .create()
            .apply {
                setColorPickerDialogListener(object : ColorPickerDialogListener {
                    override fun onDialogDismissed(dialogId: Int) {}
                    override fun onColorSelected(dialogId: Int, color: Int) {
                        Log.e("TEST-DH", Integer.toHexString(color))
                        bind.color.background = ColorDrawable(color)
                    }
                })
            }
            .show(activity.supportFragmentManager, "color-picker-dialog")
    }

    fun onDatePickClick(view: View) {
        val tv = view as TextView
        TimePickerDialogFragment.with(
            minDate = msDateFrom(),
            currDate = strYmdToLong(tv.text.toString(), systemLocale()),
            useTime = false
        ).show(
            parentFragmentManager,
            Observer {
                tv.setUnderlineText(toYMD(it, systemLocale()))
            }
        )
    }

    fun onTimePickClick(view: View) {
        val tv = view as TextView
        TimePickerDialogFragment.with(
            useDate = false
        ).show(
            parentFragmentManager,
            Observer {
                tv.setUnderlineText(toTime(it, systemLocale()))
                editor.alarmTime = it
            }
        )
    }

    fun onDoneClick() {
        if (bind.titleEditor.text.isNullOrEmpty()) {
            return
        }

        editor.edit()
        popBackStack()
    }

    fun onCancelClick() {
        popBackStack()
    }
}

