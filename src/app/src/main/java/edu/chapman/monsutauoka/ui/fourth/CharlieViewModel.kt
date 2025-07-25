package edu.chapman.monsutauoka.ui.fourth

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.chapman.monsutauoka.services.StepCounterService

class CharlieViewModel(stepCounterService: StepCounterService) : ViewModel() {
    // modify from int to string since user will be inputting their string to screen
    // through view model
    val text: MutableLiveData<String> = MutableLiveData<String>("Your Text Will Appear Here!")
    val steps: LiveData<Float> = stepCounterService.steps.asLiveData()
}
