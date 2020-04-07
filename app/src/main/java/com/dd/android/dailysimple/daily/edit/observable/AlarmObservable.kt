package com.dd.android.dailysimple.daily.edit.observable

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dd.android.dailysimple.BR
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.db.Alarm


private const val TAG = "AlarmObservable"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class AlarmObservable(alarm: Alarm) : BaseObservable() {

    var alarm: Alarm = alarm
        set(value) {
            field = value
            notifyPropertyChanged(BR.on)
            notifyPropertyChanged(BR.alarmPowerAdapterPosition)
        }

    @get:Bindable
    var isOn = false
        set(value) {
            if (field == value) return
            field = value
            notifyPropertyChanged(BR.on)
        }

    @get:Bindable
    var alarmPowerAdapterPosition: Int
        get() = when (alarm.alarmPower) {
            Alarm.Power.WEAK -> 0
            Alarm.Power.STRONG -> 2
            else -> 1
        }
        set(value) {
            alarm.alarmPower = when (value) {
                0 -> Alarm.Power.WEAK
                2 -> Alarm.Power.STRONG
                else -> Alarm.Power.NORMAL
            }
            notifyPropertyChanged(BR.alarmPowerAdapterPosition)
        }
}