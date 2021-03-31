package com.dscfast.sawari.ui.fragments.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dscfast.sawari.databinding.FragmentSplashBinding
import com.dscfast.sawari.extensions.hideStatusBar
import com.dscfast.sawari.extensions.viewLifecycle
import com.dscfast.sawari.ui.fragments.base.BaseFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment: BaseFragment() {

    private var fragmentSplashBinding: FragmentSplashBinding by viewLifecycle()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSplashBinding = FragmentSplashBinding.inflate(
            inflater,
            container,
            false
        )
        fragmentSplashBinding.apply {
            lifecycleOwner = this@SplashFragment
        }
        return fragmentSplashBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hostActivity?.hideStatusBar()
        GlobalScope.launch {
            delay(1200L)
            proceedToHomeScreen()
        }
    }

    private fun proceedToHomeScreen() {
        navigateTo(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
    }
}