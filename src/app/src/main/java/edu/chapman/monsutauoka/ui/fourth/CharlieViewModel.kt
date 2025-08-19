package edu.chapman.monsutauoka.ui.fourth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import edu.chapman.monsutauoka.services.StepCounterService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlin.math.floor

class CharlieViewModel(stepCounterService: StepCounterService) : ViewModel() {
    private var initialized = false

    private lateinit var _steps: LiveData<Float>
    val steps: LiveData<Float> = stepCounterService.steps.asLiveData()

    // sticks cannot perform math on live data, we have to map it (steps.map) to transform the given data, then we do 100f to avoid integer math :)
    private val _stickBalance = MutableStateFlow(0)
    val sticks = _stickBalance.asStateFlow().asLiveData()

    // already credited sticks
    private var creditedSticksFromSteps = 0

    // previous step reading detecting resets
    private var lastStepsValue = 0f


    init {
        stepCounterService.steps
            .onEach { steps ->
                // If the step counter resets (e.g., new day/device reboot), realign baseline
                if (steps < lastStepsValue) {
                    creditedSticksFromSteps = floor(steps / 100f).toInt()
                }

                val potential = floor(steps / 100f).toInt()   // total possible sticks from steps so far (change 100f if you want to change the way we grant our sticks)
                val changeOfSticks = (potential - creditedSticksFromSteps).coerceAtLeast(0)
                if (changeOfSticks > 0) {
                    _stickBalance.update { it + changeOfSticks }   // add newly earned sticks
                    creditedSticksFromSteps += changeOfSticks
                }

                lastStepsValue = steps
            }
            .launchIn(viewModelScope)
    }


    fun useStick(n: Int): Boolean {
        val current = _stickBalance.value
        return if (current >= n) {
            _stickBalance.value = current - n
            true
        } else false
    }


    fun initialize(service: StepCounterService) {
        if (initialized) {
            throw IllegalStateException("StepViewModel is already initialized")
        }

        _steps = service.steps.asLiveData()

        initialized = true
    }
}