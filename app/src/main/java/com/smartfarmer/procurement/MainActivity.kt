package com.smartfarmer.procurement

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.smartfarmer.procurement.domain.models.AppLanguageCode
import com.smartfarmer.procurement.presentation.components.NetworkStatusBanner
import com.smartfarmer.procurement.presentation.navigation.AppNavGraph
import com.smartfarmer.procurement.presentation.theme.SmartFarmerTheme
import com.smartfarmer.procurement.util.LocaleHelper

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val language = LocaleHelper.getPersistedLanguage(newBase)
        super.attachBaseContext(LocaleHelper.setLocale(newBase, language))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as SmartFarmerApp

        setContent {
            var currentLanguage by remember { mutableStateOf(LocaleHelper.getPersistedLanguage(this)) }
            val isConnected by app.networkMonitor.isConnected.collectAsState()
            val navController = rememberNavController()

            SmartFarmerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        NetworkStatusBanner(isConnected = isConnected)
                        Box(modifier = Modifier.weight(1f)) {
                            AppNavGraph(
                                navController = navController,
                                authRepository = app.authRepository,
                                farmerRepository = app.farmerRepository,
                                bookingRepository = app.bookingRepository,
                                queueRepository = app.queueRepository,
                                staffRepository = app.staffRepository,
                                adminRepository = app.adminRepository,
                                onLanguageChange = { newLang ->
                                    currentLanguage = newLang
                                    LocaleHelper.setLocale(this@MainActivity, newLang)
                                    recreate()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
