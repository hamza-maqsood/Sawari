package com.dscfast.sawari.ui.fragments.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dscfast.sawari.databinding.FragmentAboutBinding
import com.dscfast.sawari.extensions.viewLifecycle
import com.dscfast.sawari.ui.fragments.base.BaseFragment
import com.dscfast.sawari.ui.fragments.base.viewmodel.kodeinViewModel
import android.content.Intent
import android.net.Uri

class AboutFragment: BaseFragment() {

    private val aboutViewModel: AboutViewModel by kodeinViewModel()
    private var fragmentAboutBinding: FragmentAboutBinding by viewLifecycle()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAboutBinding = FragmentAboutBinding.inflate(
            inflater,
            container,
            false
        )
        fragmentAboutBinding.apply {
            lifecycleOwner = this@AboutFragment
            viewModel = aboutViewModel
        }
        return fragmentAboutBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observeAboutActions()
    }

    private fun observeAboutActions() {
        aboutViewModel.aboutActions.observe(viewLifecycleOwner) {
            when(it) {
                AboutActions.GoBack -> {
                    popBackToPrevious()
                }
                AboutActions.OpenEmail -> {
                    Intent(Intent.ACTION_VIEW).apply {
                        this.data = Uri.parse(
                            "mailto:dscfastisb@gmail.com?subject=Sawari App Android"
                        )
                        startActivity(this)
                    }
                }
            }
        }
    }
}