package com.smartfarmer.procurement.presentation.screens.farmer

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartfarmer.procurement.R
import com.smartfarmer.procurement.data.models.*
import com.smartfarmer.procurement.data.repository.BookingRepository
import com.smartfarmer.procurement.data.repository.FarmerRepository
import com.smartfarmer.procurement.data.repository.QueueRepository
import com.smartfarmer.procurement.domain.models.BookingStatus
import com.smartfarmer.procurement.domain.models.CropGrade
import com.smartfarmer.procurement.domain.models.PaymentStatus
import com.smartfarmer.procurement.domain.models.QueueStatus
import com.smartfarmer.procurement.domain.usecases.CalculateWaitTimeUseCase
import com.smartfarmer.procurement.presentation.components.*
import com.smartfarmer.procurement.presentation.theme.*
import com.smartfarmer.procurement.util.CurrencyUtils
import com.smartfarmer.procurement.util.DateUtils
import com.smartfarmer.procurement.util.PdfReceiptGenerator
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FarmerDashboardScreen(
    farmerId: String,
    farmerRepository: FarmerRepository,
    bookingRepository: BookingRepository,
    onNavigateToBookSlot: () -> Unit,
    onNavigateToBookingDetail: (String) -> Unit,
    onNavigateToLiveQueue: (String) -> Unit,
    onNavigateToProcurementTracker: (String) -> Unit,
    onNavigateToReceipts: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToHelp: () -> Unit
) {
    val bookings by bookingRepository.getFarmerBookings(farmerId).collectAsState(initial = emptyList())
    var profile by remember { mutableStateOf<FarmerProfile?>(null) }

    LaunchedEffect(farmerId) {
        profile = farmerRepository.getProfile(farmerId)
    }

    val activeBooking = bookings.firstOrNull { it.status == BookingStatus.CONFIRMED || it.status == BookingStatus.IN_PROGRESS }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToBookSlot,
                containerColor = GreenPrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(id = R.string.book_slot), fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
        ) {
            item {
                FarmerHeader(
                    farmerName = profile?.name ?: "Ramesh Kumar",
                    farmerId = profile?.farmerCardNo ?: "KC-RAIPUR-8842",
                    onProfileClick = onNavigateToProfile
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                // Quick Action Grid
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = stringResource(id = R.string.quick_actions),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        QuickActionItem(
                            icon = Icons.Default.CalendarToday,
                            label = stringResource(id = R.string.book_slot),
                            onClick = onNavigateToBookSlot,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        QuickActionItem(
                            icon = Icons.Default.Queue,
                            label = stringResource(id = R.string.queue_status),
                            onClick = { activeBooking?.let { onNavigateToLiveQueue(it.tokenNumber) } },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        QuickActionItem(
                            icon = Icons.Default.Timeline,
                            label = stringResource(id = R.string.procurement_status),
                            onClick = { activeBooking?.let { onNavigateToProcurementTracker(it.id) } },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        QuickActionItem(
                            icon = Icons.Default.SupportAgent,
                            label = stringResource(id = R.string.help_support),
                            onClick = onNavigateToHelp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Active Today's Slot Section
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = stringResource(id = R.string.today_slot),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (activeBooking != null) {
                        TokenCard(
                            tokenNumber = activeBooking.tokenNumber,
                            cropName = activeBooking.cropName,
                            slotTime = activeBooking.timeSlot,
                            centerName = activeBooking.centerName,
                            status = activeBooking.status,
                            onViewClick = { onNavigateToBookingDetail(activeBooking.id) }
                        )
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EventBusy,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(id = R.string.no_active_slot),
                                    color = TextSecondary,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = onNavigateToBookSlot,
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(text = stringResource(id = R.string.book_now))
                                }
                            }
                        }
                    }
                }
            }

            // Booking History
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Your Recent Bookings",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(bookings) { booking ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    TokenCard(
                        tokenNumber = booking.tokenNumber,
                        cropName = booking.cropName,
                        slotTime = booking.timeSlot,
                        centerName = booking.centerName,
                        status = booking.status,
                        onViewClick = { onNavigateToBookingDetail(booking.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun QuickActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardSurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 2,
                lineHeight = 14.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSlotScreen(
    farmerId: String,
    farmerRepository: FarmerRepository,
    bookingRepository: BookingRepository,
    onBookingSuccess: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val centers by farmerRepository.getAllCenters().collectAsState(initial = emptyList())
    val crops by farmerRepository.getActiveCrops().collectAsState(initial = emptyList())

    var selectedCenter by remember { mutableStateOf<ProcurementCenter?>(null) }
    var selectedCrop by remember { mutableStateOf<Crop?>(null) }
    var expectedQuantity by remember { mutableStateOf("50") }
    var selectedDate by remember { mutableStateOf(DateUtils.getTodayDateString()) }
    var selectedSlot by remember { mutableStateOf("09:00 AM - 10:00 AM") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val availableSlots = listOf(
        TimeSlot(slotId = "S1", startTime = "08:00 AM", endTime = "09:00 AM", totalCapacity = 20, bookedCount = 8),
        TimeSlot(slotId = "S2", startTime = "09:00 AM", endTime = "10:00 AM", totalCapacity = 20, bookedCount = 12),
        TimeSlot(slotId = "S3", startTime = "10:00 AM", endTime = "11:00 AM", totalCapacity = 20, bookedCount = 17),
        TimeSlot(slotId = "S4", startTime = "11:00 AM", endTime = "12:00 PM", totalCapacity = 20, bookedCount = 5),
        TimeSlot(slotId = "S5", startTime = "01:00 PM", endTime = "02:00 PM", totalCapacity = 20, bookedCount = 2),
        TimeSlot(slotId = "S6", startTime = "02:00 PM", endTime = "03:00 PM", totalCapacity = 20, bookedCount = 9)
    )

    LaunchedEffect(centers, crops) {
        if (selectedCenter == null && centers.isNotEmpty()) selectedCenter = centers.first()
        if (selectedCrop == null && crops.isNotEmpty()) selectedCrop = crops.first()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.book_slot), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary, titleContentColor = Color.White, navigationIconContentColor = Color.White)
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
            // 1. Select Center
            Text(text = stringResource(id = R.string.select_center), fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(8.dp))
            centers.forEach { center ->
                val isSelected = selectedCenter?.id == center.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedCenter = center },
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFE8F5E9) else Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = isSelected, onClick = { selectedCenter = center })
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = center.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(text = "📍 ${center.address}", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Select Crop
            Text(text = stringResource(id = R.string.select_crop), fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(8.dp))
            crops.forEach { crop ->
                val isSelected = selectedCrop?.id == crop.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedCrop = crop },
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFE8F5E9) else Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = isSelected, onClick = { selectedCrop = crop })
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = crop.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text(text = "MSP: ₹${crop.mspRatePerQuintal} / Quintal", fontSize = 12.sp, color = GreenPrimary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Expected Quantity
            OutlinedTextField(
                value = expectedQuantity,
                onValueChange = { expectedQuantity = it },
                label = { Text(stringResource(id = R.string.expected_quantity)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Available Slots
            Text(text = stringResource(id = R.string.available_slots), fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(8.dp))

            availableSlots.forEach { slot ->
                val slotStr = "${slot.startTime} - ${slot.endTime}"
                val isSelected = selectedSlot == slotStr
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedSlot = slotStr },
                    colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFE8F5E9) else Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = isSelected, onClick = { selectedSlot = slotStr })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = slotStr, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                        Text(
                            text = "Available: ${slot.availableCount}",
                            fontSize = 12.sp,
                            color = if (slot.availableCount < 5) StatusOrange else GreenPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMsg!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (selectedCenter != null && selectedCrop != null && expectedQuantity.toDoubleOrNull() != null) {
                        isLoading = true
                        scope.launch {
                            val res = bookingRepository.createBooking(
                                farmerId = farmerId,
                                farmerName = "Ramesh Kumar",
                                farmerMobile = "9876543210",
                                center = selectedCenter!!,
                                crop = selectedCrop!!,
                                expectedQuantity = expectedQuantity.toDouble(),
                                bookingDate = selectedDate,
                                timeSlot = selectedSlot
                            )
                            isLoading = false
                            if (res.isSuccess) {
                                onBookingSuccess(res.getOrThrow().id)
                            } else {
                                errorMsg = "Could not complete booking. Please retry."
                            }
                        }
                    } else {
                        errorMsg = "Please fill in all details."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = stringResource(id = R.string.confirm_booking), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    bookingId: String,
    bookingRepository: BookingRepository,
    onNavigateToLiveQueue: (String) -> Unit,
    onNavigateToTracker: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var booking by remember { mutableStateOf<Booking?>(null) }

    LaunchedEffect(bookingId) {
        booking = bookingRepository.getBookingById(bookingId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Booking Token & QR", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        if (booking == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else {
            val b = booking!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TOKEN NUMBER",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                Text(
                    text = b.tokenNumber,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GreenPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // QR Code Display
                QRCodeDisplay(content = b.qrCodePayload.ifEmpty { b.id })

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Show this QR code at the procurement counter",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardSurfaceLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow(label = "Booking ID", value = b.id)
                        DetailRow(label = "Procurement Centre", value = b.centerName)
                        DetailRow(label = "Produce / Crop", value = b.cropName)
                        DetailRow(label = "Expected Qty", value = "${b.expectedQuantityQuintals} Quintals")
                        DetailRow(label = "Date", value = b.bookingDate)
                        DetailRow(label = "Slot Time", value = b.timeSlot)
                        DetailRow(label = "Status", value = b.status.name)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onNavigateToLiveQueue(b.tokenNumber) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Icon(Icons.Default.AccessTime, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Track Live Queue Position", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { onNavigateToTracker(b.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Timeline, contentDescription = null, tint = GreenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "View Procurement Timeline", color = GreenPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = TextSecondary)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveQueueScreen(
    tokenNumber: String,
    queueRepository: QueueRepository,
    onNavigateBack: () -> Unit
) {
    val queueList by queueRepository.getCenterQueue("CTR_001", DateUtils.getTodayDateString()).collectAsState(initial = emptyList())
    val progress = CalculateWaitTimeUseCase.calculate(queueList, tokenNumber)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Real-Time Queue", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary, titleContentColor = Color.White, navigationIconContentColor = Color.White)
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
            LiveQueueDisplayCard(
                currentServing = progress.currentServingToken,
                yourToken = tokenNumber,
                peopleAhead = progress.peopleAhead,
                estimatedWaitMinutes = progress.estimatedWaitMinutes,
                isYourTurn = progress.isYourTurn
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Today's Queue Board",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (queueList.isEmpty()) {
                Text(text = "No other tokens in queue today", color = TextSecondary)
            } else {
                queueList.forEach { item ->
                    val isTarget = item.tokenNumber == tokenNumber
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isTarget) Color(0xFFE8F5E9) else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = item.tokenNumber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = if (isTarget) GreenPrimary else TextPrimary
                                )
                                Text(text = "${item.farmerName} • ${item.cropName}", fontSize = 12.sp, color = TextSecondary)
                            }
                            BadgeStatus(status = item.status.name)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcurementTrackerScreen(
    bookingId: String,
    bookingRepository: BookingRepository,
    onNavigateToReceipt: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var booking by remember { mutableStateOf<Booking?>(null) }

    LaunchedEffect(bookingId) {
        booking = bookingRepository.getBookingById(bookingId)
    }

    val stepIndex = when (booking?.status) {
        BookingStatus.CONFIRMED -> 0
        BookingStatus.CHECKED_IN -> 1
        BookingStatus.IN_PROGRESS -> 3
        BookingStatus.COMPLETED -> 6
        else -> 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Procurement Tracker", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Tracking ID: $bookingId",
                fontSize = 13.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(20.dp))

            StatusTimelineView(currentStep = stepIndex)

            Spacer(modifier = Modifier.height(30.dp))

            if (booking?.status == BookingStatus.COMPLETED) {
                Button(
                    onClick = {
                        val receiptNo = "REC-${booking?.bookingDate?.replace("-", "")}-${booking?.tokenNumber}"
                        onNavigateToReceipt(receiptNo)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "View Digital Procurement Receipt", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DigitalReceiptScreen(
    receiptNumber: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    val mockReceipt = Receipt(
        receiptNumber = receiptNumber.ifEmpty { "REC-20260826-A124" },
        procurementRecordId = "PR_202608260012",
        bookingId = "BK202608260012",
        tokenNumber = "A-124",
        farmerName = "Ramesh Kumar",
        farmerId = "KC-RAIPUR-8842",
        centerName = "Central Agricultural Procurement Centre",
        dateFormatted = "26 August 2026",
        cropName = "Paddy (Common / Dhan)",
        netQuantityQuintals = 50.0,
        qualityGrade = "GRADE_A",
        ratePerQuintal = 2300.0,
        totalAmount = 115000.0,
        paymentStatus = PaymentStatus.PROCESSING,
        transactionRef = "TXN891245"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Digital Receipt", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary, titleContentColor = Color.White, navigationIconContentColor = Color.White)
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "GOVERNMENT OF INDIA / AGRICULTURE DEPT",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Kisan Digital Procurement Receipt",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GreenPrimary
                    )
                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    DetailRow(label = "Receipt No", value = mockReceipt.receiptNumber)
                    DetailRow(label = "Date", value = mockReceipt.dateFormatted)
                    DetailRow(label = "Farmer Name", value = mockReceipt.farmerName)
                    DetailRow(label = "Farmer ID", value = mockReceipt.farmerId)
                    DetailRow(label = "Centre", value = mockReceipt.centerName)
                    DetailRow(label = "Crop", value = mockReceipt.cropName)
                    DetailRow(label = "Net Quantity", value = "${mockReceipt.netQuantityQuintals} Quintal")
                    DetailRow(label = "Quality Grade", value = mockReceipt.qualityGrade)
                    DetailRow(label = "MSP Rate", value = "₹${mockReceipt.ratePerQuintal} / Quintal")

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "TOTAL AMOUNT", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            text = CurrencyUtils.formatInr(mockReceipt.totalAmount),
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = GreenPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    DetailRow(label = "Payment Status", value = mockReceipt.paymentStatus.name)
                    DetailRow(label = "Transaction Ref", value = mockReceipt.transactionRef)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val pdfFile = PdfReceiptGenerator.generateReceiptPdf(context, mockReceipt)
                    if (pdfFile != null) {
                        PdfReceiptGenerator.shareReceipt(context, pdfFile)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(id = R.string.share_receipt), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerProfileScreen(
    farmerId: String,
    farmerRepository: FarmerRepository,
    onLogout: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var profile by remember { mutableStateOf<FarmerProfile?>(null) }

    LaunchedEffect(farmerId) {
        profile = farmerRepository.getProfile(farmerId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farmer Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = profile?.name ?: "Ramesh Kumar", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Farmer ID: ${profile?.farmerCardNo ?: "KC-RAIPUR-8842"}", fontSize = 13.sp, color = AccentGold)
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    DetailRow(label = "Mobile", value = profile?.mobile ?: "9876543210")
                    DetailRow(label = "Village", value = profile?.village ?: "Mandir Hasaud")
                    DetailRow(label = "District", value = profile?.district ?: "Raipur")
                    DetailRow(label = "State", value = profile?.state ?: "Chhattisgarh")
                    DetailRow(label = "Land Holding", value = "${profile?.totalLandAcres ?: 4.5} Acres")
                    DetailRow(label = "Bank A/c", value = profile?.bankAccount ?: "XXXXXX9823")
                    DetailRow(label = "IFSC", value = profile?.ifscCode ?: "SBIN0001234")
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StatusRed)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(id = R.string.logout), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.help_support), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenPrimary, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Kisan Helpline (Toll-Free)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "📞 1800-180-1551", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = GreenPrimaryDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Available 24x7 in Hindi, Chhattisgarhi, and English for procurement support.", fontSize = 13.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Frequently Asked Questions (FAQ)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(10.dp))

            FaqItem(
                question = "How do I know when to reach the procurement centre?",
                answer = "Track your live token on the app. When 5 or fewer farmers are ahead of you, proceed to the centre."
            )
            FaqItem(
                question = "How is the procurement amount calculated?",
                answer = "Total Amount = Net Approved Quantity × MSP Rate minus any moisture penalty (if moisture > 17%)."
            )
            FaqItem(
                question = "When will the payment reach my bank account?",
                answer = "Payment is credited directly via DBT within 24 to 48 hours after procurement is completed."
            )
        }
    }
}

@Composable
fun FaqItem(question: String, answer: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = "Q: $question", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = answer, fontSize = 13.sp, color = TextSecondary)
        }
    }
}
