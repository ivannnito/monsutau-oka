package edu.chapman.monsutauoka.viewmodel       //viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.chapman.monsutauoka.data.StickRepository //data source of how many sticks

//updating + observing state
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
//viewmodel for slot
class MiniGameViewModel(
    private val repo: StickRepository   //so viewmodel can access stick balance
) : ViewModel() {

    //constants: how much it costs to spin
    companion object {
        const val SPIN_COST = 1 // how many sticks a spin costs
    }

    //ossible emoji outcomes
    private val symbols = listOf("🍒", "🍋", "🍊", "⭐", "7️⃣")

    // Slots shown on screen (current)
    private val _slots = MutableStateFlow(listOf("🍒", "🍋", "🍊")) //default
    val slots: StateFlow<List<String>> = _slots //read by UI but only changed by viewmodel

    // Expose the global wallet (flow from the repo; connects to stick balance in repo)
    val sticksFlow = repo.balanceFlow

    // Last spin result for UI (+ = profit after cost, 0 = breakeven, - = loss)
    private val _lastPayout = MutableStateFlow(0)
    val lastPayout: StateFlow<Int> = _lastPayout

    /** Main function for spins
     * first tries to spend sticks
     *  if successful, roll and grant winnings back to repo.
     *  calculates rewards and returns sticks if won
     */
    fun spinSlotMachine() {
        //launch logic in a coroutine tied to viewmodel's lifecycle
        viewModelScope.launch {
            val paid = repo.spend(SPIN_COST)  // safe, atomic in DataStore
            if (!paid) {
                _lastPayout.value = 0 // couldn't spin (insufficient sticks)
                return@launch
            }
            //roll 3 random slot symbols
            val result = List(3) { symbols.random() }
            _slots.value = result       //updates UI

            //calculates how many sticks you should get back
            val reward = calculateReward(result)
            if (reward > 0) repo.grant(reward)      //returns reward(if won)

            //calculates net gain/loss
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
