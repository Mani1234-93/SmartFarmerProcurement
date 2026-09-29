package com.smartfarmer.procurement.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.smartfarmer.procurement.data.repository.*
import com.smartfarmer.procurement.domain.models.AppLanguageCode
import com.smartfarmer.procurement.domain.models.UserRole
import com.smartfarmer.procurement.presentation.screens.admin.*
import com.smartfarmer.procurement.presentation.screens.auth.*
import com.smartfarmer.procurement.presentation.screens.farmer.*
import com.smartfarmer.procurement.presentation.screens.language.LanguageSelectionScreen
import com.smartfarmer.procurement.presentation.screens.onboarding.OnboardingScreen
import com.smartfarmer.procurement.presentation.screens.splash.SplashScreen
import com.smartfarmer.procurement.presentation.screens.staff.*

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authRepository: AuthRepository,
    farmerRepository: FarmerRepository,
    bookingRepository: BookingRepository,
    queueRepository: QueueRepository,
    staffRepository: StaffRepository,
    adminRepository: AdminRepository,
    onLanguageChange: (AppLanguageCode) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateNext = {
                    navController.navigate(Screen.LanguageSelect.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.LanguageSelect.route) {
            LanguageSelectionScreen(
                onLanguageSelected = { onLanguageChange(it) },
                onContinue = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.LanguageSelect.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinishOnboarding = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                authRepository = authRepository,
                onLoginSuccess = { role ->
                    when (role) {
                        UserRole.FARMER -> navController.navigate(Screen.FarmerDashboard.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                        UserRole.STAFF -> navController.navigate(Screen.StaffDashboard.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                        UserRole.ADMIN -> navController.navigate(Screen.AdminDashboard.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.FarmerRegister.route) },
                onNavigateToStaffLogin = { navController.navigate(Screen.StaffLogin.route) }
            )
        }

        composable(Screen.FarmerRegister.route) {
            FarmerRegisterScreen(
                authRepository = authRepository,
                onRegisterSuccess = {
                    navController.navigate(Screen.FarmerDashboard.route) {
                        popUpTo(Screen.FarmerRegister.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.StaffLogin.route) {
            StaffLoginScreen(
                authRepository = authRepository,
                onLoginSuccess = { role ->
                    when (role) {
                        UserRole.ADMIN -> navController.navigate(Screen.AdminDashboard.route) { popUpTo(Screen.StaffLogin.route) { inclusive = true } }
                        else -> navController.navigate(Screen.StaffDashboard.route) { popUpTo(Screen.StaffLogin.route) { inclusive = true } }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Farmer Destination Routes
        composable(Screen.FarmerDashboard.route) {
            FarmerDashboardScreen(
                farmerId = "FARMER_9876543210",
                farmerRepository = farmerRepository,
                bookingRepository = bookingRepository,
                onNavigateToBookSlot = { navController.navigate(Screen.BookSlot.route) },
                onNavigateToBookingDetail = { id -> navController.navigate(Screen.BookingDetail.createRoute(id)) },
                onNavigateToLiveQueue = { token -> navController.navigate(Screen.LiveQueue.createRoute(token)) },
                onNavigateToProcurementTracker = { id -> navController.navigate(Screen.ProcurementTracker.createRoute(id)) },
                onNavigateToReceipts = { rec -> navController.navigate(Screen.DigitalReceipt.createRoute(rec)) },
                onNavigateToProfile = { navController.navigate(Screen.FarmerProfile.route) },
                onNavigateToHelp = { navController.navigate(Screen.HelpSupport.route) }
            )
        }

        composable(Screen.BookSlot.route) {
            BookSlotScreen(
                farmerId = "FARMER_9876543210",
                farmerRepository = farmerRepository,
                bookingRepository = bookingRepository,
                onBookingSuccess = { id ->
                    navController.navigate(Screen.BookingDetail.createRoute(id)) {
                        popUpTo(Screen.BookSlot.route) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.BookingDetail.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("bookingId") ?: ""
            BookingDetailScreen(
                bookingId = id,
                bookingRepository = bookingRepository,
                onNavigateToLiveQueue = { token -> navController.navigate(Screen.LiveQueue.createRoute(token)) },
                onNavigateToTracker = { bId -> navController.navigate(Screen.ProcurementTracker.createRoute(bId)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.LiveQueue.route,
            arguments = listOf(navArgument("tokenNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val token = backStackEntry.arguments?.getString("tokenNumber") ?: ""
            LiveQueueScreen(
                tokenNumber = token,
                queueRepository = queueRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ProcurementTracker.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("bookingId") ?: ""
            ProcurementTrackerScreen(
                bookingId = id,
                bookingRepository = bookingRepository,
                onNavigateToReceipt = { rec -> navController.navigate(Screen.DigitalReceipt.createRoute(rec)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.DigitalReceipt.route,
            arguments = listOf(navArgument("receiptNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val rec = backStackEntry.arguments?.getString("receiptNumber") ?: ""
            DigitalReceiptScreen(
                receiptNumber = rec,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.FarmerProfile.route) {
            FarmerProfileScreen(
                farmerId = "FARMER_9876543210",
                farmerRepository = farmerRepository,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.HelpSupport.route) {
            HelpSupportScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Staff Routes
        composable(Screen.StaffDashboard.route) {
            StaffDashboardScreen(
                staffRepository = staffRepository,
                onNavigateToQueue = { navController.navigate(Screen.StaffQueue.route) },
                onNavigateToScan = { navController.navigate(Screen.QRScanner.route) },
                onNavigateToProduceIntake = { id -> navController.navigate(Screen.ProduceIntake.createRoute(id)) },
                onNavigateToDailySummary = {},
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.StaffQueue.route) {
            StaffQueueScreen(
                staffRepository = staffRepository,
                queueRepository = queueRepository,
                onNavigateBack = { navController.popBackStack() },
                onFarmerSelect = { id -> navController.navigate(Screen.ProduceIntake.createRoute(id)) }
            )
        }

        composable(Screen.QRScanner.route) {
            QRScannerScreen(
                staffRepository = staffRepository,
                onScanSuccess = { id -> navController.navigate(Screen.ProduceIntake.createRoute(id)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ProduceIntake.route,
            arguments = listOf(navArgument("bookingId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("bookingId") ?: ""
            ProduceIntakeScreen(
                bookingId = id,
                bookingRepository = bookingRepository,
                staffRepository = staffRepository,
                onIntakeComplete = { recNo ->
                    navController.navigate(Screen.DigitalReceipt.createRoute(recNo))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Admin Routes
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                adminRepository = adminRepository,
                onNavigateToCenters = { navController.navigate(Screen.CenterManagement.route) },
                onNavigateToCrops = { navController.navigate(Screen.CropPriceManagement.route) },
                onNavigateToCapacity = { navController.navigate(Screen.CenterManagement.route) },
                onNavigateToReports = { navController.navigate(Screen.ReportsAnalytics.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.CenterManagement.route) {
            CenterManagementScreen(
                adminRepository = adminRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.CropPriceManagement.route) {
            CropPriceManagementScreen(
                adminRepository = adminRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ReportsAnalytics.route) {
            ReportsAnalyticsScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
