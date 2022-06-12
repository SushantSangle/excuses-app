package xyz.sushant.excusesapp.base

import android.app.Application
import xyz.sushant.excusesapp.base.network.ApiClient

class ExcusesApp: Application() {
    override fun onCreate() {
        super.onCreate()
        ApiClient.createService(applicationContext)
    }
}