package com.dscfast.sawari.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dscfast.sawari.R
import com.google.android.gms.location.LocationRequest
import timber.log.Timber

class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LocationRequest.PRIORITY_HIGH_ACCURACY -> {
                if (resultCode == RESULT_OK) {
                    Timber.d("Status: On")
                    mOnLocationSettingsChangedListener?.onLocationSettingsChanged(turnedOn = true)
                } else {
                    Timber.d("Status: Off")
                    mOnLocationSettingsChangedListener?.onLocationSettingsChanged(turnedOn = false)
                }
            }
        }
    }

    interface OnLocationSettingsChangedListener {
        fun onLocationSettingsChanged(turnedOn: Boolean)
    }

    companion object {
        private var mOnLocationSettingsChangedListener: OnLocationSettingsChangedListener? = null

        fun bindOnLocationSettingsChangedListener(listener: OnLocationSettingsChangedListener) {
            mOnLocationSettingsChangedListener = listener
        }
    }
}