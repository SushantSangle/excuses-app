package xyz.sushant.excusesapp.base

import android.app.Application
import xyz.sushant.excusesapp.base.network.ApiClient

class ExcusesApp: Application() {
    override fun onCreate() {
        super.onCreate()
        app = this
        ApiClient.createService(applicationContext)
    }

    override fun onTerminate() {
        app = null
        super.onTerminate()
    }

    companion object {
        private var app: ExcusesApp? = null

        @JvmStatic
        fun getInstance() = app
    }
}