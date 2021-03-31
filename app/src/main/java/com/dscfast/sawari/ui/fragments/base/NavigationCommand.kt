package com.dscfast.sawari.ui.fragments.base

import android.os.Bundle
import androidx.navigation.NavDirections

sealed class NavigationCommand {
    data class To(val directions: NavDirections, val bundle: Bundle?, val popBackCurrentOne: Boolean): NavigationCommand()
    data class Back(val fragmentToSkip: Int?): NavigationCommand()
    data class BackTo(val destinationId: Int): NavigationCommand()
}