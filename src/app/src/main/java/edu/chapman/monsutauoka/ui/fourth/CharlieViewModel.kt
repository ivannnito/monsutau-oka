package edu.chapman.monsutauoka.ui.fourth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import edu.chapman.monsutauoka.services.StepCounterService

class CharlieViewModel(stepCounterService: StepCounterService) : ViewModel() {
    private var initialized = false

    private lateinit var _steps: LiveData<Float>
    val steps: LiveData<Float> = stepCounterService.steps.asLiveData()

    fun initialize(service: StepCounterService) {
        if (initialized) {
            throw IllegalStateException("StepViewModel is already initialized")
        }

        _steps = service.steps.asLiveData()

        initialized = true
    }
}