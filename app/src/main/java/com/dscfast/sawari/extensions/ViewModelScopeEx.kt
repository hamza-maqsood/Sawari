package com.dscfast.sawari.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

fun ViewModel.ioQuery(handler: CoroutineExceptionHandler? = null, block: suspend () -> Unit) {
    if (handler != null) viewModelScope.launch(Dispatchers.IO + handler) {
        block()
    } else viewModelScope.launch(Dispatchers.IO) {
        block()
    }
}