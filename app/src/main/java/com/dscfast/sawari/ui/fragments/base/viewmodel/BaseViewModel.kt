package com.dscfast.sawari.ui.fragments.base.viewmodel

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.dscfast.sawari.utils.SingleLiveEvent
import com.dscfast.sawari.ui.fragments.base.NavigationCommand

open class BaseViewModel: ViewModel() {

    val navigationCommand: SingleLiveEvent<NavigationCommand> = SingleLiveEvent()

    fun navigate(directions: NavDirections, bundle: Bundle?, popBackPreviousOne: Boolean) {
        navigationCommand.postValue(
            NavigationCommand.To(
                directions,
                bundle,
                popBackPreviousOne
            )
        )
    }

    fun popBack(fragmentToSkipUpTo: Int?) {
        navigationCommand.postValue(NavigationCommand.Back(fragmentToSkipUpTo))
    }
}