package com.smartfarmer.procurement.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object LanguageSelect : Screen("language_select")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object FarmerRegister : Screen("farmer_register")
    object StaffLogin : Screen("staff_login")

    // Farmer Routes
    object FarmerDashboard : Screen("farmer_dashboard")
    object BookSlot : Screen("book_slot")
    object BookingDetail : Screen("booking_detail/{bookingId}") {
        fun createRoute(bookingId: String) = "booking_detail/$bookingId"
    }
    object LiveQueue : Screen("live_queue/{tokenNumber}") {
        fun createRoute(tokenNumber: String) = "live_queue/$tokenNumber"
    }
    object ProcurementTracker : Screen("procurement_tracker/{bookingId}") {
        fun createRoute(bookingId: String) = "procurement_tracker/$bookingId"
    }
    object DigitalReceipt : Screen("digital_receipt/{receiptNumber}") {
        fun createRoute(receiptNumber: String) = "digital_receipt/$receiptNumber"
    }
    object FarmerProfile : Screen("farmer_profile")
    object HelpSupport : Screen("help_support")

    // Staff Routes
    object StaffDashboard : Screen("staff_dashboard")
    object StaffQueue : Screen("staff_queue")
    object QRScanner : Screen("qr_scanner")
    object ProduceIntake : Screen("produce_intake/{bookingId}") {
        fun createRoute(bookingId: String) = "produce_intake/$bookingId"
    }
    object StaffDailySummary : Screen("staff_daily_summary")

    // Admin Routes
    object AdminDashboard : Screen("admin_dashboard")
    object CenterManagement : Screen("center_management")
    object CropPriceManagement : Screen("crop_price_management")
    object SlotCapacityConfig : Screen("slot_capacity_config")
    object UserManagement : Screen("user_management")
    object ReportsAnalytics : Screen("reports_analytics")
}
