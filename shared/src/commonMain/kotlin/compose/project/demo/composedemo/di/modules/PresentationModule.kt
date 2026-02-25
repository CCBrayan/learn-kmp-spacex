package compose.project.demo.composedemo.di.modules

import compose.project.demo.composedemo.presentation.rocketLaunch.RocketLaunchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


    val presentationModule = module {
        viewModel {
            RocketLaunchViewModel(get())}
    }
