package com.smartfarmer.procurement.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartfarmer.procurement.domain.models.BookingStatus
import com.smartfarmer.procurement.domain.models.QueueStatus
import com.smartfarmer.procurement.presentation.theme.*
import com.smartfarmer.procurement.util.QRHelper

@Composable
fun FarmerHeader(
    farmerName: String,
    farmerId: String,
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = GreenPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "नमस्ते / Welcome",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                        Text(
                            text = farmerName.ifEmpty { "Kisan Bhai" },
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (farmerId.isNotEmpty()) {
                            Text(
                                text = "ID: $farmerId",
                                color = AccentGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                IconButton(onClick = onNotificationClick) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun TokenCard(
    tokenNumber: String,
    cropName: String,
    slotTime: String,
    centerName: String,
    status: BookingStatus,
    onViewClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TOKEN NUMBER",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = tokenNumber,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GreenPrimary
                    )
                }
                BadgeStatus(status = status.name)
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp), color = Color(0xFFEEEEEE))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Crop", fontSize = 12.sp, color = TextSecondary)
                    Text(text = cropName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Slot Time", fontSize = 12.sp, color = TextSecondary)
                    Text(text = slotTime, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "📍 $centerName",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun BadgeStatus(status: String) {
    val (bg, fg) = when (status) {
        "CONFIRMED", "COMPLETED" -> Pair(Color(0xFFE8F5E9), GreenPrimary)
        "CALLED", "IN_PROGRESS", "CHECKED_IN" -> Pair(Color(0xFFFFF3E0), StatusOrange)
        "CANCELLED", "NO_SHOW" -> Pair(Color(0xFFFFEBEE), StatusRed)
        else -> Pair(Color(0xFFE3F2FD), StatusBlue)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = status, color = fg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LiveQueueDisplayCard(
    currentServing: String,
    yourToken: String,
    peopleAhead: Int,
    estimatedWaitMinutes: Int,
    isYourTurn: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isYourTurn) Color(0xFFFFF8E1) else CardSurfaceLight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isYourTurn) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentGold)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🔔 IT'S YOUR TURN! PROCEED TO COUNTER",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "YOUR TOKEN", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    Text(text = yourToken, fontSize = 28.sp, fontWeight = FontWeight.Black, color = GreenPrimary)
                }
                Divider(
                    modifier = Modifier
                        .height(50.dp)
                        .width(1.dp),
                    color = Color.LightGray
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "NOW SERVING", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                    Text(text = currentServing, fontSize = 28.sp, fontWeight = FontWeight.Black, color = StatusOrange)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Groups, contentDescription = null, tint = GreenPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(text = "People Ahead", fontSize = 11.sp, color = TextSecondary)
                        Text(text = "$peopleAhead Farmers", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.HourglassTop, contentDescription = null, tint = AccentGold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(text = "Estimated Wait", fontSize = 11.sp, color = TextSecondary)
                        Text(text = "$estimatedWaitMinutes Minutes", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusTimelineView(currentStep: Int) {
    val steps = listOf(
        "Booking Confirmed",
        "Farmer Arrived",
        "Verified & Weighed",
        "Quality Graded",
        "Procurement Done",
        "Payment Initiated",
        "Payment Credited"
    )

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        steps.forEachIndexed { index, step ->
            val isDone = index <= currentStep
            val isCurrent = index == currentStep

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isDone -> GreenPrimary
                                isCurrent -> AccentGold
                                else -> Color.LightGray
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Text(
                            text = "${index + 1}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = step,
                    fontSize = 14.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = if (isDone) TextPrimary else TextSecondary
                )
            }
            if (index < steps.size - 1) {
                Box(
                    modifier = Modifier
                        .padding(start = 11.dp)
                        .width(2.dp)
                        .height(20.dp)
                        .background(if (index < currentStep) GreenPrimary else Color.LightGray)
                )
            }
        }
    }
}

@Composable
fun QRCodeDisplay(content: String, modifier: Modifier = Modifier) {
    val bitmap: Bitmap? = QRHelper.generateQRCodeBitmap(content, 400)
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Booking QR Code",
            modifier = modifier
                .size(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, GreenPrimary, RoundedCornerShape(16.dp))
                .padding(8.dp)
        )
    } else {
        Box(
            modifier = modifier.size(220.dp).background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "QR Code Generation Error")
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, fontSize = 12.sp, color = TextSecondary)
        }
    }
}

@Composable
fun NetworkStatusBanner(isConnected: Boolean) {
    if (!isConnected) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(StatusOrange)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.WifiOff, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Offline Mode: Showing cached data",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
