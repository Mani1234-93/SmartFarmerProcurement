# Smart Farmer Procurement Queue & Slot Booking System

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.0-blue.svg)](https://kotlinlang.org)
[![Android Gradle Plugin](https://img.shields.io/badge/AGP-8.5.2-green.svg)](https://developer.android.com/studio/releases/gradle-plugin)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-brightgreen.svg)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-Firestore%20%7C%20Auth%20%7C%20FCM-orange.svg)](https://firebase.google.com)
[![Room DB](https://img.shields.io/badge/Room-Offline%20Cache-purple.svg)](https://developer.android.com/training/data-storage/room)

A digital platform designed to eliminate congestion, long physical queues, and uncertainty at agricultural procurement centres (Mandis / Samitis). The system empowers farmers to book advance time slots, monitor their queue position in real time, receive turn notifications, and track produce weighing, grading, and direct bank payments.

---

## Key Features

- **Tri-Role Architecture**:
  - 🌾 **Farmer**: Slot booking, Token & QR generation, Live queue position tracking, Estimated wait time, Step-by-step procurement timeline, Digital PDF receipts, Multi-language support.
  - 🏢 **Procurement Staff**: Live queue board, "Call Next Farmer", QR barcode scanner for identity verification, Produce weighing & grading intake, Instant receipt generation.
  - 🏛️ **Administrator**: Statewide intake dashboard, Center capacity management, Crop Minimum Support Price (MSP) and moisture policy configuration, Audit logs & DBT reconciliation.
- **Real-Time Queue Management**: Real-time queue algorithms calculating "Now Serving", "People Ahead", and "Estimated Wait Time".
- **Multi-Language Accessibility**: Runtime language switching for **English**, **हिंदी (Hindi)**, and **छत्तीसगढ़ी (Chhattisgarhi)** with high-contrast UI and large touch targets ($\ge 48\text{dp}$).
- **Offline Resilience**: Offline caching via Room database with automatic background synchronization upon network reconnection.
- **Digital PDF Receipts**: In-app PDF generation using Android `PdfDocument` API for instant viewing, printing, and WhatsApp sharing.

---

## Directory Structure

```text
SmartFarmerProcurement/
├── app/
│   ├── build.gradle.kts                 # Dependencies (Compose M3, Room, Firebase, ZXing, CameraX)
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml      # Permissions, activities, FCM service, FileProvider
│       │   ├── java/com/smartfarmer/procurement/
│       │   │   ├── SmartFarmerApp.kt    # Application entry & DB initialization
│       │   │   ├── MainActivity.kt      # Main host activity with dynamic locale support
│       │   │   ├── data/
│       │   │   │   ├── models/          # User, FarmerProfile, Center, Crop, Booking, Queue, Receipt
│       │   │   │   ├── local/           # Room Database, Converters, DAOs (Booking, Queue, Crop, etc.)
│       │   │   │   └── repository/      # Auth, Farmer, Booking, Queue, Staff, Admin repositories
│       │   │   ├── domain/
│       │   │   │   ├── models/          # Domain enums (UserRole, BookingStatus, QueueStatus, etc.)
│       │   │   │   └── usecases/        # TokenGenerator, CalculateWaitTime, PricingCalculator
│       │   │   ├── presentation/
│       │   │   │   ├── components/      # FarmerHeader, TokenCard, QueueDisplay, StatusTimeline, QR
│       │   │   │   ├── navigation/      # Screen routes & NavGraph
│       │   │   │   ├── theme/           # Agricultural color palette, typography & M3 theme
│       │   │   │   └── screens/
│       │   │   │       ├── splash/      # Animated splash screen
│       │   │   │       ├── language/    # Multi-language selector
│       │   │   │       ├── onboarding/  # 3-step agricultural workflow slides
│       │   │   │       ├── auth/        # Login, Farmer Registration, Staff Login
│       │   │   │       ├── farmer/      # Dashboard, BookSlot, Detail, LiveQueue, Tracker, Receipts
│       │   │   │       ├── staff/       # Desk, QueueManager, QRScanner, ProduceIntake
│       │   │   │       └── admin/       # Analytics, CenterConfig, CropPrices, Reports
│       │   │   └── util/                # LocaleHelper, QRHelper, PdfReceiptGenerator, NetworkMonitor
│       │   └── res/
│       │       ├── values/              # English UI strings & agricultural color tokens
│       │       ├── values-hi/           # Hindi localization
│       │       ├── values-hne/          # Chhattisgarhi localization
│       │       └── xml/                 # File provider & data extraction rules
│       └── test/java/com/smartfarmer/procurement/
│           └── SmartFarmerUnitTests.kt  # Token generation, Pricing calculations, Queue tests
├── backend/
│   ├── package.json
│   ├── server.js                        # Node.js / Express REST API alternative
│   └── .env.example
├── firestore/
│   ├── firestore.rules                  # Role-based security rules
│   ├── firestore.indexes.json           # Composite query indexes
│   ├── seed_data.json                   # Pre-configured centers and MSP crops
│   └── seed.js                          # Automated database seeder
├── documentation/
│   ├── ARCHITECTURE.md                  # System architecture design document
│   ├── DATABASE_SCHEMA.md               # Detailed database models & tables
│   ├── FIREBASE_SETUP_GUIDE.md          # Step-by-step Firebase deployment guide
│   └── USER_AND_STAFF_MANUAL.md         # Farmer, Staff, and Admin operational manual
└── README.md
```

---

## Getting Started

### 1. Opening the Project in Android Studio
1. Open **Android Studio** (Koala / Ladybug or newer recommended).
2. Select **Open** and choose the `SmartFarmerProcurement` folder.
3. Allow Gradle to sync dependencies automatically.
4. Ensure JDK 17 is configured under **Settings** $\rightarrow$ **Build, Execution, Deployment** $\rightarrow$ **Build Tools** $\rightarrow$ **Gradle**.

### 2. Firebase Configuration
1. Create a project in [Firebase Console](https://console.firebase.google.com/).
2. Add an Android app with package name: `com.smartfarmer.procurement`.
3. Download `google-services.json` and place it in the `app/` folder.
4. Enable **Cloud Firestore** and **Authentication** (Phone & Email).
5. Deploy security rules and indexes:
   ```bash
   firebase deploy --only firestore:rules,firestore:indexes
   ```
6. Seed the initial database:
   ```bash
   cd firestore
   npm install
   node seed.js
   ```

### 3. Running the REST API Backend (Optional)
```bash
cd backend
npm install
cp .env.example .env
npm start
```
The server will start on `http://localhost:5000`.

---

## Running Automated Tests

Run the unit test suite covering token algorithms, moisture price penalties, and queue calculations:

```bash
# In project root
./gradlew test
```

---

## Building Release Artifacts

### 1. Generate Signed APK
```bash
./gradlew assembleRelease
```
The output APK will be located at:
`app/build/outputs/apk/release/app-release-unsigned.apk`

### 2. Generate Android App Bundle (AAB for Google Play)
```bash
./gradlew bundleRelease
```
The output AAB will be located at:
`app/build/outputs/bundle/release/app-release.aab`

---

## License
Developed for Agricultural Digital Procurement & Queue Management Systems.
