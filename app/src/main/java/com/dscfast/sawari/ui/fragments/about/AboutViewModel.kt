package com.dscfast.sawari.ui.fragments.about

import com.dscfast.sawari.ui.fragments.base.viewmodel.BaseViewModel
import com.dscfast.sawari.utils.SingleLiveEvent

class AboutViewModel: BaseViewModel() {

    val aboutActions: SingleLiveEvent<AboutActions> = SingleLiveEvent()

    fun goBack() {
        aboutActions.postValue(AboutActions.GoBack)
    }

    fun openEmail() {
        aboutActions.postValue(AboutActions.OpenEmail)
    }
}