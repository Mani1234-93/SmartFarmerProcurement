package com.smartfarmer.procurement.presentation.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartfarmer.procurement.R
import com.smartfarmer.procurement.data.models.Crop
import com.smartfarmer.procurement.data.models.DashboardSummary
import com.smartfarmer.procurement.data.models.ProcurementCenter
import com.smartfarmer.procurement.data.repository.AdminRepository
import com.smartfarmer.procurement.presentation.components.DetailRow
import com.smartfarmer.procurement.presentation.components.MetricCard
import com.smartfarmer.procurement.presentation.theme.*
import com.smartfarmer.procurement.util.CurrencyUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    adminRepository: AdminRepository,
    onNavigateToCenters: () -> Unit,
    onNavigateToCrops: () -> Unit,
    onNavigateToCapacity: () -> Unit,
    onNavigateToReports: () -> Unit,
    onLogout: () -> Unit
) {
    var summary by remember { mutableStateOf(DashboardSummary(
        totalFarmers = 1250,
        todayBookings = 320,
        completedToday = 214,
        waitingInQueue = 72,
        cancelledToday = 34,
        totalProcurementAmount = 1850000.0,
        totalQuantityProcured = 804.5
    )) }

    LaunchedEffect(Unit) {
        summary = adminRepository.getDashboardSummary()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Government Administrator Portal", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EarthBrown, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = "Statewide Agricultural Procurement Metrics", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(16.dp))

            // Metric Cards Grid
            Row(modifier = Modifier.fillMaxWidth()) {
                MetricCard(
                    title = "Total Registered Farmers",
                    value = "${summary.totalFarmers}",
                    icon = Icons.Default.Groups,
                    color = GreenPrimary,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                MetricCard(
                    title = "Today's Bookings",
                    value = "${summary.todayBookings}",
                    icon = Icons.Default.CalendarToday,
                    color = StatusBlue,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                MetricCard(
                    title = "Procurement Completed",
                    value = "${summary.completedToday}",
                    icon = Icons.Default.CheckCircle,
                    color = StatusGreen,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                MetricCard(
                    title = "Currently Waiting",
                    value = "${summary.waitingInQueue}",
                    icon = Icons.Default.HourglassTop,
                    color = StatusOrange,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Total Amount Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "TOTAL PROCUREMENT DISBURSED (DBT)", fontSize = 12.sp, color = GreenPrimaryDark, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = CurrencyUtils.formatInr(summary.totalProcurementAmount),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GreenPrimaryDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Administrative Modules", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            AdminMenuOption(title = "Procurement Centres Management", subtitle = "Add centres, operating hours & daily capacity", icon = Icons.Default.Storefront, onClick = onNavigateToCenters)
            AdminMenuOption(title = "Crop MSP & Moisture Policies", subtitle = "Configure support prices and quality rules", icon = Icons.Default.Spa, onClick = onNavigateToCrops)
            AdminMenuOption(title = "Slot Capacity Configuration", subtitle = "Configure hourly tokens per centre", icon = Icons.Default.Tune, onClick = onNavigateToCapacity)
            AdminMenuOption(title = "Procurement Reports & Analytics", subtitle = "View daily CSV audit logs and DBT reconciliations", icon = Icons.Default.Analytics, onClick = onNavigateToReports)
        }
    }
}

@Composable
fun AdminMenuOption(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Color(0xFFEFEBE9), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = EarthBrown)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = subtitle, fontSize = 12.sp, color = TextSecondary)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterManagementScreen(
    adminRepository: AdminRepository,
    onNavigateBack: () -> Unit
) {
    val centers by adminRepository.getAllCenters().collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newCode by remember { mutableStateOf("") }
    var newCapacity by remember { mutableStateOf("1500") }
    var newAddress by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Procurement Centres", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EarthBrown, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = GreenPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Center")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(centers) { center ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = center.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(text = center.code, color = GreenPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "📍 ${center.address}", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "Capacity: ${center.dailyCapacityQuintals} Quintals / Day", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "Hours: ${center.operatingHours}", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Add Procurement Centre") },
                text = {
                    Column {
                        OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Centre Name") })
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = newCode, onValueChange = { newCode = it }, label = { Text("Centre Code (e.g. CPC-05)") })
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = newCapacity, onValueChange = { newCapacity = it }, label = { Text("Daily Capacity (Quintals)") })
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = newAddress, onValueChange = { newAddress = it }, label = { Text("Address & District") })
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newName.isNotBlank() && newCode.isNotBlank()) {
                                scope.launch {
                                    adminRepository.addCenter(
                                        ProcurementCenter(
                                            id = "CTR_${System.currentTimeMillis() % 1000}",
                                            code = newCode,
                                            name = newName,
                                            address = newAddress,
                                            dailyCapacityQuintals = newCapacity.toDoubleOrNull() ?: 1000.0
                                        )
                                    )
                                    showDialog = false
                                }
                            }
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropPriceManagementScreen(
    adminRepository: AdminRepository,
    onNavigateBack: () -> Unit
) {
    val crops by adminRepository.getAllCrops().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crop MSP & Quality Rules", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EarthBrown, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(crops) { crop ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = crop.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = crop.category, color = AccentGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "Official MSP: ₹${crop.mspRatePerQuintal} / Quintal", fontWeight = FontWeight.Bold, color = GreenPrimary, fontSize = 14.sp)
                        Text(text = "Acceptable Moisture Limit: Up to ${crop.acceptableMoistureMaxPct}%", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "Variety: ${crop.variety}", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsAnalyticsScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports & Reconciliation", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EarthBrown, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Daily Procurement Audit Logs", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Export consolidated CSV records for State Civil Supplies & DBT bank payment reconciliation.", fontSize = 13.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { /* Export CSV */ },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export CSV Report")
                    }
                }
            }
        }
    }
}
