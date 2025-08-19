package edu.chapman.monsutauoka.services.data.di

import android.content.Context
import edu.chapman.monsutauoka.data.StickRepository
import edu.chapman.monsutauoka.services.StepCounterService

object ServiceLocator {
    @Volatile private var _stickRepo: StickRepository? = null
    val stickRepo: StickRepository
        get() = _stickRepo ?: error("ServiceLocator not initialized")

    fun init(appContext: Context, stepCounterService: StepCounterService) {
        if (_stickRepo == null) {
            synchronized(this) {
                if (_stickRepo == null) {
                    _stickRepo = StickRepository(appContext, stepCounterService)
                }
            }
        }
    }
}
