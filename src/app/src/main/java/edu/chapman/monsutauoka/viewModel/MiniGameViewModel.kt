package edu.chapman.monsutauoka.viewmodel
// No Step conversion yet
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import edu.chapman.monsutauoka.model.TreatStore

class MiniGameViewModel(private val treatStore: TreatStore) : ViewModel() {

    private val _dollars = MutableStateFlow(5) // Start with 5 dollars for testing
    val dollars: StateFlow<Int> = _dollars

    private val _slots = MutableStateFlow(listOf("🍒", "🍋", "🍊"))
    val slots: StateFlow<List<String>> = _slots

    private val symbols = listOf("🍒", "🍋", "🍊", "⭐", "7️⃣")

    fun spinSlotMachine() {
        if (_dollars.value < 1) return
        _dollars.value--

        val result = List(3) { symbols.random() }
        _slots.value = result

        val reward = calculateReward(result)
        if (reward > 0) treatStore.addTreats(reward)
    }

    private fun calculateReward(slot: List<String>): Int {
        return when {
            slot.toSet().size == 1 -> 5 // All 3 match
            slot.distinct().size == 2 -> 2 // Two match
            else -> 0
        }
    }

    fun addFakeMoneyForDebugging(amount: Int = 5) {
        _dollars.value += amount
    }
}
