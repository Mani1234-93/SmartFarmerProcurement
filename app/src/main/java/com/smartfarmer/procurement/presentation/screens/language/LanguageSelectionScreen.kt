package com.smartfarmer.procurement.presentation.screens.language

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartfarmer.procurement.R
import com.smartfarmer.procurement.domain.models.AppLanguageCode
import com.smartfarmer.procurement.presentation.theme.GreenPrimary
import com.smartfarmer.procurement.presentation.theme.TextPrimary
import com.smartfarmer.procurement.presentation.theme.TextSecondary

@Composable
fun LanguageSelectionScreen(
    onLanguageSelected: (AppLanguageCode) -> Unit,
    onContinue: () -> Unit
) {
    var selectedLanguage by remember { mutableStateOf(AppLanguageCode.ENGLISH) }

    val languages = listOf(
        Triple(AppLanguageCode.ENGLISH, "English", "Select English language"),
        Triple(AppLanguageCode.HINDI, "हिंदी (Hindi)", "हिंदी भाषा चुनें"),
        Triple(AppLanguageCode.CHHATTISGARHI, "छत्तीसगढ़ी (Chhattisgarhi)", "छत्तीसगढ़ी भाखा चुनव")
    )

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    onLanguageSelected(selectedLanguage)
                    onContinue()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(
                    text = stringResource(id = R.string.continue_btn),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = "Language",
                tint = GreenPrimary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.select_language),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(32.dp))

            languages.forEach { (lang, title, subtitle) ->
                val isSelected = selectedLanguage == lang
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) GreenPrimary else Color.LightGray,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { selectedLanguage = lang },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFFE8F5E9) else Color.White
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) GreenPrimary else TextPrimary
                            )
                            Text(
                                text = subtitle,
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = GreenPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
