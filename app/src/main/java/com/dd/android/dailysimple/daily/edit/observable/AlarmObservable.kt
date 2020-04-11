package com.dd.android.dailysimple.daily.edit.observable

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.dd.android.dailysimple.BR
import com.dd.android.dailysimple.common.Logger
import com.dd.android.dailysimple.db.data.Alarm


private const val TAG = "AlarmObservable"
private inline fun logD(crossinline message: () -> String) = Logger.d(TAG, message)

class AlarmObservable(alarm: Alarm) : BaseObservable() {

    var alarm: Alarm = alarm
        set(value) {
            val old = field
            field = value
            if (field.alarmPower != old.alarmPower) {
                notifyPropertyChanged(BR.alarmPowerAdapterPosition)
            }
            if (field.alarmTime != old.alarmTime) {
                notifyPropertyChanged(BR.alarmTime)
            }
            if (field.alarmDays != old.alarmDays) {
                notifyPropertyChanged(BR.alarmDays)
            }
        }

    @get:Bindable
    var isOn = false
        set(value) {
            if (field == value) return
            field = value
            notifyPropertyChanged(BR.on)
        }

    @get:Bindable
    var alarmTime: Int
        get() = alarm.alarmTime
        set(value) {
            alarm.alarmTime = value
            notifyPropertyChanged(BR.alarmTime)
        }

    @get:Bindable
    var alarmDays: Int
        get() = alarm.alarmDays
        set(value) {
            alarm.alarmDays = value
            notifyPropertyChanged(BR.alarmDays)
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