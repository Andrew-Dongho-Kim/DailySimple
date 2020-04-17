package com.dd.android.dailysimple.common

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

typealias onClick = ((View) -> Unit)


class FabViewModel : ViewModel() {
    val isOpen = liveData {
        emit(false)
    } as MutableLiveData<Boolean>

    val fab1Text = MutableLiveData<String>()

    val fab2Text = MutableLiveData<String>()

    val fab3Text = MutableLiveData<String>()

    var onFab1Click: onClick? = null
        set(onClick) {
            field = { view ->
                onClick?.invoke(view)
                toggle()
            }
        }
    var onFab2Click: onClick? = null
        set(onClick) {
            field = { view ->
                onClick?.invoke(view)
                toggle()
            }
        }
    var onFab3Click: onClick? = null
        set(onClick) {
            field = { view ->
                onClick?.invoke(view)
                toggle()
            }
        }

    @JvmOverloads
    fun toggle(view: View? = null) {
        viewModelScope.launch {
            isOpen.postValue(isOpen.value?.let { !it } ?: true)
        }
    }
}
