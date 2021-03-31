package com.dscfast.sawari.utils

import android.content.Context
import android.widget.Toast

/**
 * displays a normal toast
 */
fun Context.toast(message: String) {
    val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
    toast.show()
}