package edu.chapman.monsutauoka.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import edu.chapman.monsutauoka.data.StickRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class WalletViewModel(private val repo: StickRepository) : ViewModel() {

    val sticks: LiveData<Int> = repo.balanceFlow.asLiveData()

    // One-shot UI feedback (e.g., not enough sticks)
    private val _spendResults = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
    val spendResults = _spendResults.asSharedFlow()

    fun spend(n: Int) {
        viewModelScope.launch {
            val ok = repo.spend(n)
            _spendResults.tryEmit(ok)
        }
    }

    fun grant(n: Int) {
        viewModelScope.launch { repo.grant(n) }
    }
}
