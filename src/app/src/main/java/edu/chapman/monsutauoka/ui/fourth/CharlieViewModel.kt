package edu.chapman.monsutauoka.ui.fourth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import edu.chapman.monsutauoka.services.StepCounterService
import kotlin.math.floor

class CharlieViewModel(stepCounterService: StepCounterService) : ViewModel() {
    private var initialized = false

    private lateinit var _steps: LiveData<Float>
    val steps: LiveData<Float> = stepCounterService.steps.asLiveData()

    // sticks cannot perform math on live data, we have to map it (steps.map) to transform the given data, then we do 100f to avoid integer math :)
    val sticks: LiveData<Int> = steps.map { floor(it / 100f).toInt() }

    fun initialize(service: StepCounterService) {
        if (initialized) {
            throw IllegalStateException("StepViewModel is already initialized")
        }

        _steps = service.steps.asLiveData()

        initialized = true
    }
}