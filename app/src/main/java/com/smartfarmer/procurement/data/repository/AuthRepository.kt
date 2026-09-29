package com.smartfarmer.procurement.data.repository

import com.smartfarmer.procurement.data.local.AppDatabase
import com.smartfarmer.procurement.data.models.FarmerProfile
import com.smartfarmer.procurement.data.models.User
import com.smartfarmer.procurement.domain.models.UserRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.util.UUID

class AuthRepository(private val db: AppDatabase) {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    suspend fun login(mobile: String, pinOrPass: String, role: UserRole): Result<User> = withContext(Dispatchers.IO) {
        try {
            // Check local users or create demo session
            var user = db.userDao().getUserById(mobile)
            if (user == null) {
                // If demo staff/admin credentials or existing farmer
                val defaultName = when (role) {
                    UserRole.ADMIN -> "Chief Procurement Officer"
                    UserRole.STAFF -> "Centre Officer (Raipur)"
                    UserRole.FARMER -> "Ramesh Kumar"
                }
                user = User(
                    id = if (role == UserRole.FARMER) "FARMER_$mobile" else "STAFF_$mobile",
                    name = defaultName,
                    mobile = mobile,
                    role = role,
                    centerId = if (role == UserRole.STAFF) "CTR_001" else null
                )
                db.userDao().insertUser(user)

                if (role == UserRole.FARMER) {
                    val profile = FarmerProfile(
                        id = user.id,
                        name = user.name,
                        mobile = mobile,
                        farmerCardNo = "KC-RAIPUR-8842",
                        village = "Mandir Hasaud",
                        district = "Raipur",
                        state = "Chhattisgarh",
                        preferredCenterId = "CTR_001",
                        bankAccount = "XXXXXX9823",
                        ifscCode = "SBIN0001234",
                        totalLandAcres = 4.5
                    )
                    db.farmerDao().insertFarmerProfile(profile)
                }
            }
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerFarmer(
        name: String,
        mobile: String,
        farmerCardNo: String,
        village: String,
        district: String,
        state: String,
        preferredCenterId: String,
        bankAccount: String,
        ifscCode: String,
        landAcres: Double
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            val userId = "FARMER_$mobile"
            val newUser = User(
                id = userId,
                name = name,
                mobile = mobile,
                role = UserRole.FARMER
            )
            val profile = FarmerProfile(
                id = userId,
                name = name,
                mobile = mobile,
                farmerCardNo = farmerCardNo,
                village = village,
                district = district,
                state = state,
                preferredCenterId = preferredCenterId,
                bankAccount = bankAccount,
                ifscCode = ifscCode,
                totalLandAcres = landAcres
            )

            db.userDao().insertUser(newUser)
            db.farmerDao().insertFarmerProfile(profile)
            _currentUser.value = newUser
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        db.userDao().clearUsers()
        _currentUser.value = null
    }

    suspend fun restoreSession(): User? = withContext(Dispatchers.IO) {
        // Can be restored from shared prefs or local db
        _currentUser.value
    }
}
