package com.example

import android.app.Application
import com.example.data.local.PocketBizDatabase
import com.example.data.local.SettingsDataStore
import com.example.data.repository.BusinessRepository

class AppContainer(application: Application) {
    val database: PocketBizDatabase by lazy {
        PocketBizDatabase.getInstance(application)
    }
    val settingsDataStore: SettingsDataStore by lazy {
        SettingsDataStore(application)
    }
    val businessRepository: BusinessRepository by lazy {
        BusinessRepository(
            transactionDao = database.transactionDao(),
            customerDao = database.customerDao(),
            invoiceDao = database.invoiceDao(),
            receiptDao = database.receiptDao(),
            settingsDataStore = settingsDataStore
        )
    }
}

class PocketBizApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
