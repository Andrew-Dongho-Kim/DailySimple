package com.dd.android.dailysimple.common

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment

abstract class BaseDialogFragment<B : ViewDataBinding> : DialogFragment() {

    private lateinit var _activity: AppCompatActivity
    protected val activity get() = _activity

    private lateinit var _bind: B
    protected val bind get() = _bind

    @get:LayoutRes
    abstract val layout: Int

    @Suppress("deprecation")
    @CallSuper
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        _activity = activity as AppCompatActivity
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _bind = DataBindingUtil.inflate(inflater, layout, container, false)
        _bind.lifecycleOwner = viewLifecycleOwner
        return bind.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.let { window ->
                window.attributes = window.attributes?.let {
                    it.width = WindowManager.LayoutParams.MATCH_PARENT
                    it.gravity = Gravity.BOTTOM
                    it.flags = it.flags and FLAG_DIM_BEHIND.inv()
                    it
                }
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                window.setWindowAnimations(android.R.anim.cycle_interpolator)
            }

        }
    }
}
