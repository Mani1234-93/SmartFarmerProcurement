package com.smartfarmer.procurement.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.smartfarmer.procurement.data.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        FarmerProfile::class,
        ProcurementCenter::class,
        Crop::class,
        Booking::class,
        QueueItem::class,
        ProcurementRecord::class,
        Receipt::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun farmerDao(): FarmerDao
    abstract fun centerDao(): CenterDao
    abstract fun cropDao(): CropDao
    abstract fun bookingDao(): BookingDao
    abstract fun queueDao(): QueueDao
    abstract fun procurementDao(): ProcurementDao
    abstract fun receiptDao(): ReceiptDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_farmer_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                seedInitialData(getInstance(context))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(database: AppDatabase) {
            // Seed default procurement centers
            val initialCenters = listOf(
                ProcurementCenter(
                    id = "CTR_001",
                    code = "CPC-01",
                    name = "Central Agricultural Procurement Centre",
                    address = "Main Mandi Road, Sector 4, Raipur",
                    district = "Raipur",
                    state = "Chhattisgarh",
                    contactPhone = "+91-771-2489100",
                    dailyCapacityQuintals = 1500.0,
                    operatingHours = "08:00 AM - 05:00 PM"
                ),
                ProcurementCenter(
                    id = "CTR_002",
                    code = "KPC-02",
                    name = "Kisan Krishi Upaj Mandi Samiti",
                    address = "NH-53, Mandir Hasaud",
                    district = "Raipur",
                    state = "Chhattisgarh",
                    contactPhone = "+91-771-2983112",
                    dailyCapacityQuintals = 1200.0,
                    operatingHours = "08:30 AM - 04:30 PM"
                ),
                ProcurementCenter(
                    id = "CTR_003",
                    code = "DPC-03",
                    name = "Durg Zila Seva Sahakari Samiti",
                    address = "Station Road, Durg",
                    district = "Durg",
                    state = "Chhattisgarh",
                    contactPhone = "+91-788-2321456",
                    dailyCapacityQuintals = 1000.0,
                    operatingHours = "09:00 AM - 05:00 PM"
                ),
                ProcurementCenter(
                    id = "CTR_004",
                    code = "BPC-04",
                    name = "Bilaspur Krishi Upaj Mandi",
                    address = "Tifra Industrial Area, Bilaspur",
                    district = "Bilaspur",
                    state = "Chhattisgarh",
                    contactPhone = "+91-7752-254100",
                    dailyCapacityQuintals = 1800.0,
                    operatingHours = "08:00 AM - 05:00 PM"
                )
            )
            database.centerDao().insertCenters(initialCenters)

            // Seed default MSP crops
            val initialCrops = listOf(
                Crop(
                    id = "CROP_PADDY_COMMON",
                    name = "Paddy (Common / Dhan)",
                    nameHindi = "धान (सामान्य)",
                    variety = "Swarna / MTU 1010",
                    mspRatePerQuintal = 2300.0,
                    acceptableMoistureMaxPct = 17.0,
                    category = "Kharif"
                ),
                Crop(
                    id = "CROP_PADDY_GRADE_A",
                    name = "Paddy (Grade A)",
                    nameHindi = "धान (ग्रेड-ए)",
                    variety = "Mahamaya / HMT",
                    mspRatePerQuintal = 2320.0,
                    acceptableMoistureMaxPct = 17.0,
                    category = "Kharif"
                ),
                Crop(
                    id = "CROP_WHEAT",
                    name = "Wheat (Gehun)",
                    nameHindi = "गेहूं (शरबती)",
                    variety = "Sharbati / Lokwan",
                    mspRatePerQuintal = 2275.0,
                    acceptableMoistureMaxPct = 12.0,
                    category = "Rabi"
                ),
                Crop(
                    id = "CROP_MAIZE",
                    name = "Maize (Makka)",
                    nameHindi = "मक्का",
                    variety = "Hybrid Yellow",
                    mspRatePerQuintal = 2090.0,
                    acceptableMoistureMaxPct = 14.0,
                    category = "Kharif"
                ),
                Crop(
                    id = "CROP_SOYBEAN",
                    name = "Soybean",
                    nameHindi = "सोयाबीन (पीला)",
                    variety = "JS 9560",
                    mspRatePerQuintal = 4892.0,
                    acceptableMoistureMaxPct = 12.0,
                    category = "Kharif"
                )
            )
            database.cropDao().insertCrops(initialCrops)
        }
    }
}
