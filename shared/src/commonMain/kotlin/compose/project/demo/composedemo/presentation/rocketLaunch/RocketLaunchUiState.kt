package compose.project.demo.composedemo.presentation.rocketLaunch

import compose.project.demo.composedemo.domain.entity.RocketLaunch

data class RocketLaunchUiState(
    val isLoading: Boolean = false,
    val launches: List<RocketLaunch> = emptyList(),
)