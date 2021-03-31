package com.dscfast.sawari.di

import androidx.lifecycle.ViewModelProvider
import com.dscfast.sawari.ui.fragments.about.AboutViewModel
import com.dscfast.sawari.ui.fragments.base.viewmodel.BaseViewModel
import com.dscfast.sawari.ui.fragments.home.HomeViewModel
import com.dscfast.sawari.ui.fragments.base.viewmodel.BaseViewModelFactory
import com.dscfast.sawari.ui.fragments.base.viewmodel.bindViewModel
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

private const val MODULE_NAME = "ViewModels Module"

val viewModelsModule = Kodein.Module(
    name = MODULE_NAME,
    allowSilentOverride = false,
    prefix = "app"
) {

    bind<ViewModelProvider.Factory>() with singleton {
        BaseViewModelFactory(injector = kodein.direct)
    }

    bindViewModel<BaseViewModel>() with provider {
        BaseViewModel()
    }

    bindViewModel<HomeViewModel>() with provider {
        HomeViewModel(keyValuePairsService = instance())
    }

    bindViewModel<AboutViewModel>() with provider {
        AboutViewModel()
    }
}
