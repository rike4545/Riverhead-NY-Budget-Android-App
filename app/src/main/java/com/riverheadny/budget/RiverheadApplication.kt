package com.riverheadny.budget

import android.app.Application
import com.riverheadny.budget.data.AssetRepository

/** Lightweight repository access without a full DI framework — appropriate at this app's current scale. */
class RiverheadApplication : Application() {
    val repository: AssetRepository by lazy { AssetRepository(assets) }

    companion object {
        lateinit var instance: RiverheadApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
