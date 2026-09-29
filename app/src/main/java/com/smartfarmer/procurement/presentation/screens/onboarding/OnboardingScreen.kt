package com.smartfarmer.procurement.presentation.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartfarmer.procurement.R
import com.smartfarmer.procurement.presentation.theme.AccentGold
import com.smartfarmer.procurement.presentation.theme.GreenPrimary
import com.smartfarmer.procurement.presentation.theme.TextPrimary
import com.smartfarmer.procurement.presentation.theme.TextSecondary

data class OnboardingPageData(
    val titleRes: Int,
    val descRes: Int,
    val icon: ImageVector
)

@Composable
fun OnboardingScreen(onFinishOnboarding: () -> Unit) {
    var currentPage by remember { mutableStateOf(0) }

    val pages = listOf(
        OnboardingPageData(
            titleRes = R.string.onboarding_1_title,
            descRes = R.string.onboarding_1_desc,
            icon = Icons.Default.CalendarMonth
        ),
        OnboardingPageData(
            titleRes = R.string.onboarding_2_title,
            descRes = R.string.onboarding_2_desc,
            icon = Icons.Default.AccessTime
        ),
        OnboardingPageData(
            titleRes = R.string.onboarding_3_title,
            descRes = R.string.onboarding_3_desc,
            icon = Icons.Default.Payments
        )
    )

    val current = pages[currentPage]

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onFinishOnboarding) {
                    Text(
                        text = stringResource(id = R.string.skip),
                        color = TextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Indicator Dots
                Row {
                    pages.indices.forEach { index ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(if (index == currentPage) 24.dp else 10.dp, 10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(if (index == currentPage) GreenPrimary else Color.LightGray)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            currentPage++
                        } else {
                            onFinishOnboarding()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    modifier = Modifier.height(50.dp)
                ) {
                    Text(
                        text = if (currentPage == pages.size - 1) stringResource(id = R.string.get_started) else stringResource(id = R.string.next),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = current.icon,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = stringResource(id = current.titleRes),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = current.descRes),
                fontSize = 15.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}
