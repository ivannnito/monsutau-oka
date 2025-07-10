package edu.chapman.monsutauoka.ui.fourth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class CharlieViewModel : ViewModel() {
    // modify from int to string since user will be inputting their string to screen
    // through view model
    val text: MutableLiveData<String> = MutableLiveData<String>("Your Text Will Appear Here!")
}
