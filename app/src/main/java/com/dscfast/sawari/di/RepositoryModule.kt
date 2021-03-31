package com.dscfast.sawari.di

import com.dscfast.sawari.repo.keyvaluepairs.KeyValuePairsServiceContract
import com.dscfast.sawari.repo.keyvaluepairs.KeyValuePairsServiceContractImpl
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

private const val MODULE_NAME = "Repository Module"

val repositoryModule = Kodein.Module(
    name = MODULE_NAME,
    allowSilentOverride = false,
    prefix = "app"
) {

    bind<KeyValuePairsServiceContract>() with singleton {
        KeyValuePairsServiceContractImpl(context = instance())
    }
}
