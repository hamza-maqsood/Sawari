package com.dscfast.sawari.ui.fragments.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.dscfast.sawari.ui.activity.AppActivity
import com.dscfast.sawari.ui.fragments.base.viewmodel.BaseViewModel
import com.dscfast.sawari.ui.fragments.base.viewmodel.kodeinViewModel
import kotlinx.coroutines.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment
    : Fragment(),
    KodeinAware,
    CoroutineScope by MainScope() {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    override val kodein: Kodein by closestKodein()
    private val baseViewModel: BaseViewModel by kodeinViewModel()

    protected val hostActivity: AppActivity? by lazy {
        requireActivity() as AppActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeNavigationCommands()
    }

    private fun observeNavigationCommands() {
        baseViewModel.navigationCommand.observe(viewLifecycleOwner) { command: NavigationCommand? ->
            when (command!!) {
                is NavigationCommand.To -> {
                    findNavController().navigate((command as NavigationCommand.To).directions.actionId, command.bundle)
                }

                is NavigationCommand.Back -> {
                    if ((command as NavigationCommand.Back).fragmentToSkip != null) {
                        command.fragmentToSkip!!.let {
                            try {
                                findNavController().popBackStack(it, false)
                                Timber.d("Error popping back fragment: $it")
                            } catch (ex: Exception) {
                                findNavController().popBackStack()
                            }
                        }
                    } else {
                        findNavController().popBackStack()
                    }
                }

                is NavigationCommand.BackTo -> {
                    findNavController().navigate((command as NavigationCommand.BackTo).destinationId)
                }
            }
        }
    }

    protected fun navigateTo(direction: NavDirections, bundle: Bundle? = null, popBackPreviousOne: Boolean = false) {
        baseViewModel.navigate(direction, bundle, popBackPreviousOne)
    }

    protected fun popBackToPrevious(fragmentToSkipUpTo: Int? = null) {
        baseViewModel.popBack(fragmentToSkipUpTo)
    }

    override fun onDestroy() {
        Timber.d("On Destroyed called for base fragment")
        super.onDestroy()
        cancel()
    }
}