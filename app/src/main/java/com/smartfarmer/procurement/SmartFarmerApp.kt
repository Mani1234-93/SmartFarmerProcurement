package com.smartfarmer.procurement

import android.app.Application
import com.smartfarmer.procurement.data.local.AppDatabase
import com.smartfarmer.procurement.data.repository.*
import com.smartfarmer.procurement.util.LocaleHelper
import com.smartfarmer.procurement.util.NetworkMonitor

class SmartFarmerApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var authRepository: AuthRepository
        private set

    lateinit var farmerRepository: FarmerRepository
        private set

    lateinit var bookingRepository: BookingRepository
        private set

    lateinit var queueRepository: QueueRepository
        private set

    lateinit var staffRepository: StaffRepository
        private set

    lateinit var adminRepository: AdminRepository
        private set

    lateinit var networkMonitor: NetworkMonitor
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Apply saved language locale
        LocaleHelper.setLocale(this, LocaleHelper.getPersistedLanguage(this))

        // Initialize Room Database
        database = AppDatabase.getInstance(this)

        // Initialize Repositories
        authRepository = AuthRepository(database)
        farmerRepository = FarmerRepository(database)
        bookingRepository = BookingRepository(database)
        queueRepository = QueueRepository(database)
        staffRepository = StaffRepository(database)
        adminRepository = AdminRepository(database)
        networkMonitor = NetworkMonitor(this)
    }

    companion object {
        lateinit var instance: SmartFarmerApp
            private set
    }
}
