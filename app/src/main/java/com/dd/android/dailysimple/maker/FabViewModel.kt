package com.dd.android.dailysimple.maker

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

    val isKeyboardOpened = liveData {
        emit(false)
    } as MutableLiveData<Boolean>

    val text1 = MutableLiveData<String>()
    val text2 = MutableLiveData<String>()
    val text3 = MutableLiveData<String>()

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

    fun onFabAddClick(view: View) {
        if (isKeyboardOpened.value == true) {

        } else {
            toggle(view)
        }
    }

    @JvmOverloads
    fun toggle(view: View? = null) {
        viewModelScope.launch {
            isOpen.postValue(isOpen.value?.let { !it } ?: true)
        }
    }
}
