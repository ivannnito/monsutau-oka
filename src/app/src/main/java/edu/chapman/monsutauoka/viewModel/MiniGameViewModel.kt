package edu.chapman.monsutauoka.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.chapman.monsutauoka.data.StickRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MiniGameViewModel(
    private val repo: StickRepository
) : ViewModel() {

    companion object {
        const val SPIN_COST = 1 // how many sticks a spin costs
    }

    private val symbols = listOf("🍒", "🍋", "🍊", "⭐", "7️⃣")

    // Slots shown on screen
    private val _slots = MutableStateFlow(listOf("🍒", "🍋", "🍊"))
    val slots: StateFlow<List<String>> = _slots

    // Expose the global wallet (flow from the repo)
    val sticksFlow = repo.balanceFlow

    // Last spin result for UI (+ = profit after cost, 0 = breakeven, - = loss)
    private val _lastPayout = MutableStateFlow(0)
    val lastPayout: StateFlow<Int> = _lastPayout

    /** Pay cost from repo; if successful, roll and grant winnings back to repo. */
    fun spinSlotMachine() {
        viewModelScope.launch {
            val paid = repo.spend(SPIN_COST)  // suspend, atomic in DataStore
            if (!paid) {
                _lastPayout.value = 0 // couldn't spin (insufficient sticks)
                return@launch
            }

            val result = List(3) { symbols.random() }
            _slots.value = result

            val reward = calculateReward(result)
            if (reward > 0) repo.grant(reward)

            _lastPayout.value = reward - SPIN_COST
        }
    }

    private fun calculateReward(slot: List<String>): Int = when {
        slot[0] == slot[1] && slot[1] == slot[2] -> 5 // three of a kind
        slot[0] == slot[1] || slot[1] == slot[2] || slot[0] == slot[2] -> 2 // any pair
        else -> 0
    }

    // Optional debug helper
    fun addDebugSticks(amount: Int = 5) = viewModelScope.launch { repo.grant(amount) }
}
