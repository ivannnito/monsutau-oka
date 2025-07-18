package edu.chapman.monsutauoka.model
//Treat persistence
import android.content.Context

class TreatStore(context: Context) {
    private val prefs = context.getSharedPreferences("monster_prefs", Context.MODE_PRIVATE)

    fun addTreats(amount: Int) {
        val current = prefs.getInt("treats", 0)
        prefs.edit().putInt("treats", current + amount).apply()
    }

    fun getTreats(): Int = prefs.getInt("treats", 0)
}
