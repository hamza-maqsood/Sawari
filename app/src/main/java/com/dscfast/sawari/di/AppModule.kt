package com.dscfast.sawari.di

import android.content.res.Resources
import com.dscfast.sawari.application.SawariApp
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

private const val MODULE_NAME = "App Module"

val appModule = Kodein.Module(
    name = MODULE_NAME,
    allowSilentOverride = false,
    prefix = "app"
) {

    bind<Resources>() with singleton {
        instance<SawariApp>().resources
    }
}