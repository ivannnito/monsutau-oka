package edu.chapman.monsutauoka.ui.fourth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CharlieViewModel {
    class CharlieViewModel : ViewModel() {
        val num: MutableLiveData<Int> = MutableLiveData<Int>(0)
    }
}