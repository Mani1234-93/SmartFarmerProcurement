# Architecture & Technical Design

## 1. System Architecture

```text
                               +-----------------------------+
                               |     Android Application     |
                               | (Jetpack Compose & Kotlin)  |
                               +--------------+--------------+
                                              |
               +------------------------------+------------------------------+
               |                                                             |
               v                                                             v
+-------------------------------+                           +-------------------------------+
|      Local Room Database      |                           |   Firebase Cloud Services     |
|   (Offline Cache & Sync)      |                           | - Authentication (OTP / PIN)  |
| - Centers & Active Crops      |                           | - Firestore (Live Listeners)  |
| - Farmer Bookings & Tokens    |                           | - Cloud Messaging (FCM alerts)|
| - Procurement Records & Bills |                           | - Firebase Storage (PDFs)     |
+-------------------------------+                           +-------------------------------+
                                                                             ^
                                                                             |
                                                            +----------------+--------------+
                                                            | Node.js / Express REST Server |
                                                            | (Enterprise Integration)      |
                                                            +-------------------------------+
```

## 2. Layered Component Design

### Presentation Layer
- **Jetpack Compose + Material 3**: Agriculture-focused theme with accessible contrast, high-touch targets, and multilingual typography.
- **StateFlow & ViewModels**: Unidirectional Data Flow (UDF) ensuring robust UI state rendering during configuration changes and network reconnects.
- **NavHost**: Seamless multi-role routing between Farmer, Staff, and Admin portals.

### Domain Layer
- **Use Cases**:
  - `TokenGenerator`: Sequential token allocation preventing race conditions.
  - `CalculateWaitTimeUseCase`: Real-time queue turnaround algorithms.
  - `PricingCalculator`: Multi-grade MSP calculation with automated moisture penalties.
- **Pure Kotlin**: Decoupled from Android framework for $100\%$ unit test coverage.

### Data Layer
- **Repository Pattern**: Single source of truth abstracting Room database and Firestore/REST backends.
- **Offline Caching**: Room DAO caches all active bookings, centres, and crops, enabling farmers to view tokens and receipts without internet.
