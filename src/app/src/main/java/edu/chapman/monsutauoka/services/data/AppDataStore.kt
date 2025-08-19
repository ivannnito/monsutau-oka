package edu.chapman.monsutauoka.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore



// Top-level extension (must be top-level, not inside a class)
val Context.appDataStore by preferencesDataStore(name = "app_prefs")
