package com.dscfast.sawari.application

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.dscfast.sawari.BuildConfig
import com.dscfast.sawari.di.appModule
import com.dscfast.sawari.di.repositoryModule
import com.dscfast.sawari.di.viewModelsModule
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import timber.log.Timber

class SawariApp : MultiDexApplication(), KodeinAware {

    override val kodein: Kodein
        get() = Kodein.lazy {

            bind<Context>("ApplicationContext") with singleton {
                this@SawariApp.applicationContext
            }

            bind<SawariApp>() with singleton {
                this@SawariApp
            }

            import(appModule)
            import(viewModelsModule)
            import(repositoryModule)
        }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}