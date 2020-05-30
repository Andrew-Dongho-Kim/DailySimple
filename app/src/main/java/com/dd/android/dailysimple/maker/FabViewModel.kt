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

    val selected = liveData {
        emit(0)
    } as MutableLiveData<Int>

    var onFab1Click: onClick? = null
        set(onClick) {
            field = { view ->
                if (isKeyboardOpened.value == true) {
                    selected.postValue(0)
                } else {
                    onClick?.invoke(view)
                    toggle(view)
                }
            }
        }
    var onFab2Click: onClick? = null
        set(onClick) {
            field = { view ->
                if (isKeyboardOpened.value == true) {
                    selected.postValue(1)
                } else {
                    onClick?.invoke(view)
                    toggle(view)
                }
            }
        }
    var onFab3Click: onClick? = null
        set(onClick) {
            field = { view ->
                if (isKeyboardOpened.value == true) {
                    selected.postValue(2)
                } else {
                    onClick?.invoke(view)
                    toggle(view)
                }
            }
        }


    var onFabAddClick: onClick? = null
        set(onClick) {
            field = { view ->
                if (isKeyboardOpened.value == true) {
                    onClick?.invoke(view)
                } else {
                    toggle(view)
                }
            }
        }


    @JvmOverloads
    fun toggle(view: View? = null) {
        viewModelScope.launch { isOpen.postValue(isOpen.value?.let { !it } ?: true) }
    }
}
