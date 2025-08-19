package edu.chapman.monsutauoka.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import edu.chapman.monsutauoka.services.StepCounterService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.floor

class StickRepository(
    appContext: Context,
    private val stepCounterService: StepCounterService,
    private val externalScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
) {
    private val dataStore = appContext.appDataStore

    companion object {
        private const val STEPS_PER_STICK = 100f
        private val KEY_BALANCE = intPreferencesKey("stick_balance")
        private val KEY_CREDITED = intPreferencesKey("stick_units_credited")
    }

    /** Public balance as Flow<Int> that fragments/ViewModels can observe. */
    val balanceFlow: Flow<Int> = dataStore.data.map { it[KEY_BALANCE] ?: 0 }

    init {
        // Start accruing sticks as steps come in.
        externalScope.launch {
            stepCounterService.steps.collectLatest { rawSteps ->
                val totalEarnableUnits = floor((rawSteps.coerceAtLeast(0f)) / STEPS_PER_STICK).toInt() // float division to turn our 100 steps to 1 stick
                dataStore.edit { prefs ->
                    val alreadyCredited = prefs[KEY_CREDITED] ?: 0

                    // Handle step counter reset (e.g., new day/reboot)
                    val normalizedCredited = alreadyCredited.coerceAtMost(totalEarnableUnits)

                    val newlyEarned = (totalEarnableUnits - normalizedCredited).coerceAtLeast(0)
                    if (newlyEarned > 0) {
                        prefs[KEY_BALANCE] = (prefs[KEY_BALANCE] ?: 0) + newlyEarned
                        prefs[KEY_CREDITED] = normalizedCredited + newlyEarned
                    } else if (normalizedCredited != alreadyCredited) {
                        // Only resets case: realign baseline without changing balance
                        prefs[KEY_CREDITED] = normalizedCredited
                    }
                }
            }
        }
    }

    /** Spend N sticks; returns true if success. */
    suspend fun spend(n: Int): Boolean {
        var success = false
        dataStore.edit { prefs ->
            val bal = prefs[KEY_BALANCE] ?: 0
            if (bal >= n) {
                prefs[KEY_BALANCE] = bal - n
                success = true
            }
        }
        return success
    }

    /** helper to grant bonus sticks. */
    suspend fun grant(n: Int) {
        dataStore.edit { prefs ->
            prefs[KEY_BALANCE] = (prefs[KEY_BALANCE] ?: 0) + n
        }
    }
}
