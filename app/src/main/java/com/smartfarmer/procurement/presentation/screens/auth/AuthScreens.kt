package com.smartfarmer.procurement.presentation.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartfarmer.procurement.R
import com.smartfarmer.procurement.data.repository.AuthRepository
import com.smartfarmer.procurement.domain.models.UserRole
import com.smartfarmer.procurement.presentation.theme.AccentGold
import com.smartfarmer.procurement.presentation.theme.GreenPrimary
import com.smartfarmer.procurement.presentation.theme.TextPrimary
import com.smartfarmer.procurement.presentation.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authRepository: AuthRepository,
    onLoginSuccess: (UserRole) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToStaffLogin: () -> Unit
) {
    var mobile by remember { mutableStateOf("9876543210") }
    var pin by remember { mutableStateOf("1234") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Icon(
                imageVector = Icons.Default.Agriculture,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.login_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = stringResource(id = R.string.login_subtitle),
                fontSize = 14.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = mobile,
                onValueChange = { if (it.length <= 10) mobile = it },
                label = { Text(stringResource(id = R.string.mobile_number)) },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GreenPrimary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = pin,
                onValueChange = { pin = it },
                label = { Text(stringResource(id = R.string.password_pin)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimary) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    if (mobile.length == 10) {
                        isLoading = true
                        scope.launch {
                            val res = authRepository.login(mobile, pin, UserRole.FARMER)
                            isLoading = false
                            if (res.isSuccess) {
                                onLoginSuccess(UserRole.FARMER)
                            } else {
                                errorMessage = "Login failed. Please check credentials."
                            }
                        }
                    } else {
                        errorMessage = "Please enter valid 10-digit mobile number."
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
                    Text(text = stringResource(id = R.string.login), fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = onNavigateToRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = stringResource(id = R.string.farmer_register), color = GreenPrimary, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onNavigateToStaffLogin) {
                Text(
                    text = stringResource(id = R.string.staff_login),
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerRegisterScreen(
    authRepository: AuthRepository,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("Ramesh Kumar") }
    var mobile by remember { mutableStateOf("9876543210") }
    var farmerId by remember { mutableStateOf("KC-RAIPUR-8842") }
    var village by remember { mutableStateOf("Mandir Hasaud") }
    var district by remember { mutableStateOf("Raipur") }
    var state by remember { mutableStateOf("Chhattisgarh") }
    var landAcres by remember { mutableStateOf("4.5") }
    var bankAccount by remember { mutableStateOf("912345678901") }
    var ifscCode by remember { mutableStateOf("SBIN0001234") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.registration_title), fontWeight = FontWeight.Bold) },
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(id = R.string.full_name)) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = mobile,
                onValueChange = { if (it.length <= 10) mobile = it },
                label = { Text(stringResource(id = R.string.mobile_number)) },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GreenPrimary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = farmerId,
                onValueChange = { farmerId = it },
                label = { Text(stringResource(id = R.string.farmer_id)) },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = GreenPrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = village,
                    onValueChange = { village = it },
                    label = { Text(stringResource(id = R.string.village)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = district,
                    onValueChange = { district = it },
                    label = { Text(stringResource(id = R.string.district)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = landAcres,
                onValueChange = { landAcres = it },
                label = { Text("Total Agricultural Land (Acres)") },
                leadingIcon = { Icon(Icons.Default.Landscape, contentDescription = null, tint = GreenPrimary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = bankAccount,
                onValueChange = { bankAccount = it },
                label = { Text(stringResource(id = R.string.bank_account)) },
                leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = GreenPrimary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = ifscCode,
                onValueChange = { ifscCode = it.uppercase() },
                label = { Text(stringResource(id = R.string.ifsc_code)) },
                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = GreenPrimary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMsg!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && mobile.length == 10) {
                        isLoading = true
                        scope.launch {
                            val res = authRepository.registerFarmer(
                                name = name,
                                mobile = mobile,
                                farmerCardNo = farmerId,
                                village = village,
                                district = district,
                                state = state,
                                preferredCenterId = "CTR_001",
                                bankAccount = bankAccount,
                                ifscCode = ifscCode,
                                landAcres = landAcres.toDoubleOrNull() ?: 0.0
                            )
                            isLoading = false
                            if (res.isSuccess) {
                                onRegisterSuccess()
                            } else {
                                errorMsg = "Registration failed. Please retry."
                            }
                        }
                    } else {
                        errorMsg = "Please fill in all mandatory fields."
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
                    Text(text = stringResource(id = R.string.register_btn), fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffLoginScreen(
    authRepository: AuthRepository,
    onLoginSuccess: (UserRole) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedRole by remember { mutableStateOf(UserRole.STAFF) }
    var username by remember { mutableStateOf("staff_raipur") }
    var password by remember { mutableStateOf("admin123") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Staff & Admin Portal", fontWeight = FontWeight.Bold) },
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
            Spacer(modifier = Modifier.height(20.dp))
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Official Procurement Access", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(24.dp))

            // Role Toggle
            Row(modifier = Modifier.fillMaxWidth()) {
                FilterChip(
                    selected = selectedRole == UserRole.STAFF,
                    onClick = { selectedRole = UserRole.STAFF },
                    label = { Text("Procurement Staff") },
                    modifier = Modifier.weight(1f).padding(4.dp)
                )
                FilterChip(
                    selected = selectedRole == UserRole.ADMIN,
                    onClick = { selectedRole = UserRole.ADMIN },
                    label = { Text("Administrator") },
                    modifier = Modifier.weight(1f).padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Employee ID / Username") },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    isLoading = true
                    scope.launch {
                        authRepository.login(username, password, selectedRole)
                        isLoading = false
                        onLoginSuccess(selectedRole)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimaryDark),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Sign In as ${if (selectedRole == UserRole.ADMIN) "Admin" else "Staff"}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
