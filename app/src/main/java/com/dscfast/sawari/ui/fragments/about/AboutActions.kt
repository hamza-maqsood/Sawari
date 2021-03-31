package com.dscfast.sawari.ui.fragments.about

sealed class AboutActions {
    object GoBack: AboutActions()
    object OpenEmail: AboutActions()
}