package edu.chapman.monsutauoka.ui.fourth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import edu.chapman.monsutauoka.services.StepCounterService


class CharlieViewModel : ViewModel() {
    private var initialized = false

    private lateinit var _steps: LiveData<Float>
    val steps: LiveData<Float> get() = _steps

    fun initialize(service: StepCounterService) {
        if (initialized) {
            throw IllegalStateException("StepViewModel is already initialized")
        }

        _steps = service.steps.asLiveData()

        initialized = true
    }
    // modify from int to string since user will be inputting their string to screen
    // through view model
    val text: MutableLiveData<String> = MutableLiveData<String>("Your Text Will Appear Here!")
}
