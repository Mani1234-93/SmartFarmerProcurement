# Database Schema & Data Models

## Collections & Tables Overview

### 1. `users`
| Field | Type | Description |
|---|---|---|
| `id` | String (PK) | Unique User ID or mobile reference |
| `name` | String | User's full name |
| `mobile` | String | 10-digit mobile number |
| `role` | Enum (`FARMER`, `STAFF`, `ADMIN`) | Access level |
| `centerId` | String? | Assigned procurement centre ID (for staff) |
| `createdAt` | Long | Epoch timestamp |

### 2. `farmers`
| Field | Type | Description |
|---|---|---|
| `id` | String (PK) | Farmer ID |
| `name` | String | Name |
| `farmerCardNo` | String | Kisan Card / Official Reference ID |
| `village` | String | Farmer's village |
| `district` | String | District |
| `state` | String | State |
| `preferredCenterId` | String | FK to `procurement_centers` |
| `bankAccount` | String | Bank account number for DBT |
| `ifscCode` | String | Bank IFSC code |
| `totalLandAcres` | Double | Land holding |

### 3. `procurement_centers`
| Field | Type | Description |
|---|---|---|
| `id` | String (PK) | Centre identifier (e.g. `CTR_001`) |
| `code` | String | Public short code (e.g. `CPC-01`) |
| `name` | String | Centre full name |
| `address` | String | Physical address |
| `district` | String | District |
| `dailyCapacityQuintals` | Double | Max intake quota per day |
| `operatingHours` | String | Operating time window |

### 4. `crops`
| Field | Type | Description |
|---|---|---|
| `id` | String (PK) | Crop identifier |
| `name` | String | Crop name in English |
| `nameHindi` | String | Crop name in Hindi |
| `variety` | String | Specific variety / seed hybrid |
| `mspRatePerQuintal` | Double | Minimum Support Price (₹/Quintal) |
| `acceptableMoistureMaxPct` | Double | Maximum standard moisture before penalty (e.g. $17\%$) |

### 5. `bookings`
| Field | Type | Description |
|---|---|---|
| `id` | String (PK) | Booking ID e.g. `BK202608260012` |
| `farmerId` | String | Reference to farmer |
| `centerId` | String | Reference to centre |
| `cropId` | String | Reference to crop |
| `expectedQuantityQuintals` | Double | Farmer's expected produce volume |
| `bookingDate` | String | Date string `YYYY-MM-DD` |
| `timeSlot` | String | Time slot window e.g. `09:00 AM - 10:00 AM` |
| `tokenNumber` | String | Formatted token e.g. `A-124` |
| `qrCodePayload` | String | Encrypted/Compact JSON verification payload |
| `status` | Enum | `CONFIRMED`, `CHECKED_IN`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED` |

### 6. `queues`
| Field | Type | Description |
|---|---|---|
| `id` | String (PK) | Queue entry ID |
| `bookingId` | String | Reference to booking |
| `centerId` | String | Centre ID |
| `tokenNumber` | String | Token number |
| `sequenceNumber` | Int | Incremental sorting index for the day |
| `status` | Enum | `WAITING`, `CALLED`, `VERIFICATION`, `PROCUREMENT`, `COMPLETED`, `NO_SHOW` |
| `callTime` | Long? | Timestamp when staff called farmer |
| `completionTime` | Long? | Timestamp when transaction was finished |

### 7. `procurement_records` & `receipts`
| Field | Type | Description |
|---|---|---|
| `id` | String (PK) | Procurement transaction ID |
| `bookingId` | String | Reference to booking |
| `grossWeightQuintals` | Double | Gross weight measured on weighbridge |
| `tareWeightQuintals` | Double | Vehicle empty weight |
| `netWeightQuintals` | Double | Net crop weight $(\text{Gross} - \text{Tare})$ |
| `moisturePercentage` | Double | Lab moisture measurement |
| `qualityGrade` | Enum | `GRADE_A`, `GRADE_B`, `GRADE_C` |
| `ratePerQuintal` | Double | Effective rate applied |
| `moistureDeduction` | Double | Deductions for excess moisture |
| `totalPayableAmount` | Double | Final DBT payout amount |
| `paymentStatus` | Enum | `PENDING`, `PROCESSING`, `PAID` |
| `transactionRef` | String | Banking UTR / Transaction Reference |
