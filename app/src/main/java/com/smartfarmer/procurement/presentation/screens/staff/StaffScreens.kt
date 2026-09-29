package com.smartfarmer.procurement.presentation.screens.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartfarmer.procurement.R
import com.smartfarmer.procurement.data.models.Booking
import com.smartfarmer.procurement.data.models.QueueItem
import com.smartfarmer.procurement.data.repository.BookingRepository
import com.smartfarmer.procurement.data.repository.QueueRepository
import com.smartfarmer.procurement.data.repository.StaffRepository
import com.smartfarmer.procurement.domain.models.BookingStatus
import com.smartfarmer.procurement.domain.models.CropGrade
import com.smartfarmer.procurement.domain.models.QueueStatus
import com.smartfarmer.procurement.domain.usecases.PricingCalculator
import com.smartfarmer.procurement.presentation.components.BadgeStatus
import com.smartfarmer.procurement.presentation.components.DetailRow
import com.smartfarmer.procurement.presentation.theme.*
import com.smartfarmer.procurement.util.CurrencyUtils
import com.smartfarmer.procurement.util.DateUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDashboardScreen(
    staffRepository: StaffRepository,
    onNavigateToQueue: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToProduceIntake: (String) -> Unit,
    onNavigateToDailySummary: () -> Unit,
    onLogout: () -> Unit
) {
    val queueList by staffRepository.getTodayQueue("CTR_001").collectAsState(initial = emptyList())
    val nowServing = queueList.firstOrNull { it.status == QueueStatus.CALLED || it.status == QueueStatus.VERIFICATION || it.status == QueueStatus.PROCUREMENT }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Procurement Staff Desk", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimaryDark, titleContentColor = Color.White)
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
            // Centre Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GreenPrimary)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Central Procurement Centre (Raipur)", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Date: ${DateUtils.getTodayDateString()} • Capacity: 1500 Quintals", color = AccentGold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Now Serving Hero
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "NOW SERVING", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Text(
                        text = nowServing?.tokenNumber ?: "None",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = StatusOrange
                    )
                    if (nowServing != null) {
                        Text(text = "${nowServing.farmerName} • ${nowServing.cropName} (${nowServing.expectedQuantityQuintals} Q)", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onNavigateToProduceIntake(nowServing.bookingId) },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "Proceed to Weighing & Grading")
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "No active farmer being served", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Staff Action Buttons
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        scope.launch {
                            staffRepository.callNextFarmer("CTR_001", DateUtils.getTodayDateString())
                        }
                    },
                    modifier = Modifier.weight(1f).height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = stringResource(id = R.string.call_next), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = onNavigateToScan,
                    modifier = Modifier.weight(1f).height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimaryDark)
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = stringResource(id = R.string.scan_qr), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Today's Queue List", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = onNavigateToQueue) {
                    Text(text = "View Full Queue", color = GreenPrimary)
                }
            }

            queueList.take(5).forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onNavigateToProduceIntake(item.bookingId) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = item.tokenNumber, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "${item.farmerName} • ${item.cropName}", fontSize = 12.sp, color = TextSecondary)
                        }
                        BadgeStatus(status = item.status.name)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffQueueScreen(
    staffRepository: StaffRepository,
    queueRepository: QueueRepository,
    onNavigateBack: () -> Unit,
    onFarmerSelect: (String) -> Unit
) {
    val queueList by staffRepository.getTodayQueue("CTR_001").collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Queue Management", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimaryDark, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(queueList) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = item.tokenNumber, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                            BadgeStatus(status = item.status.name)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Farmer: ${item.farmerName}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(text = "Produce: ${item.cropName} (${item.expectedQuantityQuintals} Quintals)", fontSize = 12.sp, color = TextSecondary)
                        Text(text = "Slot: ${item.timeSlot}", fontSize = 12.sp, color = TextSecondary)

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        queueRepository.updateQueueStatus(item.id, QueueStatus.NO_SHOW)
                                    }
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(text = "No-Show", color = StatusRed, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { onFarmerSelect(item.bookingId) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                            ) {
                                Text(text = "Process", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRScannerScreen(
    staffRepository: StaffRepository,
    onScanSuccess: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var manualInput by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan Farmer QR", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimaryDark, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = "Scanner",
                    tint = AccentGold,
                    modifier = Modifier.size(100.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Point camera at farmer's mobile screen", fontSize = 14.sp, color = TextSecondary)

            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "OR ENTER TOKEN / BOOKING ID MANUALLY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = manualInput,
                onValueChange = { manualInput = it },
                label = { Text("e.g. A-124 or BK202608260012") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMsg!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (manualInput.isNotBlank()) {
                        scope.launch {
                            val booking = staffRepository.verifyFarmerByBooking(manualInput.trim())
                            if (booking != null) {
                                onScanSuccess(booking.id)
                            } else {
                                // Demo fallback
                                onScanSuccess("BK202608260012")
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(text = "Verify & Open Intake", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProduceIntakeScreen(
    bookingId: String,
    bookingRepository: BookingRepository,
    staffRepository: StaffRepository,
    onIntakeComplete: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var grossWeight by remember { mutableStateOf("52.5") }
    var tareWeight by remember { mutableStateOf("2.5") }
    var moisturePct by remember { mutableStateOf("14.0") }
    var selectedGrade by remember { mutableStateOf(CropGrade.GRADE_A) }
    var mspRate by remember { mutableStateOf("2300.0") }
    var isProcessing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val gross = grossWeight.toDoubleOrNull() ?: 0.0
    val tare = tareWeight.toDoubleOrNull() ?: 0.0
    val moisture = moisturePct.toDoubleOrNull() ?: 14.0
    val rate = mspRate.toDoubleOrNull() ?: 2300.0

    val calc = PricingCalculator.calculate(
        grossWeight = gross,
        tareWeight = tare,
        baseMspRate = rate,
        moisturePct = moisture,
        grade = selectedGrade
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weighing & Quality Intake", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimaryDark, titleContentColor = Color.White, navigationIconContentColor = Color.White)
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
            Text(text = "Booking Ref: $bookingId", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // Gross Weight
            OutlinedTextField(
                value = grossWeight,
                onValueChange = { grossWeight = it },
                label = { Text("Gross Weight (Quintals)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Tare Weight
            OutlinedTextField(
                value = tareWeight,
                onValueChange = { tareWeight = it },
                label = { Text("Tare / Vehicle Weight (Quintals)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Moisture %
            OutlinedTextField(
                value = moisturePct,
                onValueChange = { moisturePct = it },
                label = { Text("Moisture Percentage (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Grade Selection
            Text(text = "Quality Grade", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(modifier = Modifier.fillMaxWidth()) {
                CropGrade.values().forEach { grade ->
                    FilterChip(
                        selected = selectedGrade == grade,
                        onClick = { selectedGrade = grade },
                        label = { Text(grade.name.replace("_", " ")) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Real-Time Calculation Preview Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Procurement Calculation Summary", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = GreenPrimaryDark)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    DetailRow(label = "Net Approved Weight", value = "${calc.netWeightQuintals} Quintals")
                    DetailRow(label = "Effective Rate", value = "₹${calc.ratePerQuintal} / Q")
                    DetailRow(label = "Moisture Deduction", value = "₹${calc.moistureDeductionAmount}")
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "TOTAL PAYABLE", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        Text(
                            text = CurrencyUtils.formatInr(calc.totalPayableAmount),
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = GreenPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isProcessing = true
                    scope.launch {
                        val mockBooking = Booking(
                            id = bookingId,
                            farmerId = "FARMER_9876543210",
                            farmerName = "Ramesh Kumar",
                            farmerMobile = "9876543210",
                            centerId = "CTR_001",
                            centerName = "Central Procurement Centre",
                            cropId = "CROP_PADDY_COMMON",
                            cropName = "Paddy (Common / Dhan)",
                            expectedQuantityQuintals = calc.netWeightQuintals,
                            bookingDate = DateUtils.getTodayDateString(),
                            timeSlot = "09:00 AM - 10:00 AM",
                            tokenNumber = "A-124",
                            status = BookingStatus.COMPLETED
                        )
                        val res = staffRepository.completeProcurement(
                            booking = mockBooking,
                            grossWeight = gross,
                            tareWeight = tare,
                            moisturePct = moisture,
                            grade = selectedGrade,
                            baseMspRate = rate,
                            staffId = "STAFF_RAIPUR_01"
                        )
                        isProcessing = false
                        if (res.isSuccess) {
                            onIntakeComplete(res.getOrThrow().second.receiptNumber)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Complete Procurement & Issue Receipt", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
