const express = require('express');
const cors = require('cors');
require('dotenv').config();

const app = express();
app.use(cors());
app.use(express.json());

const PORT = process.env.PORT || 5000;

// Health Check Endpoint
app.get('/api/health', (req, res) => {
  res.json({
    status: 'OK',
    service: 'Smart Farmer Procurement API',
    timestamp: new Date().toISOString()
  });
});

// Authentication Middleware
const authenticateToken = (req, res, next) => {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  if (!token) return res.status(401).json({ error: 'Access token required' });

  // For development demo / JWT verify
  req.user = { id: 'FARMER_9876543210', role: 'FARMER' };
  next();
};

// 1. Auth Endpoint
app.post('/api/auth/login', (req, res) => {
  const { mobile, pin, role } = req.body;
  if (!mobile) return res.status(400).json({ error: 'Mobile number is required' });

  res.json({
    token: 'mock_jwt_token_sample_2026',
    user: {
      id: `USER_${mobile}`,
      mobile,
      name: role === 'ADMIN' ? 'State Director' : (role === 'STAFF' ? 'Raipur Centre Incharge' : 'Ramesh Kumar'),
      role: role || 'FARMER'
    }
  });
});

// 2. Centres Endpoint
app.get('/api/centers', (req, res) => {
  res.json([
    {
      id: 'CTR_001',
      code: 'CPC-01',
      name: 'Central Agricultural Procurement Centre',
      address: 'Main Mandi Road, Sector 4, Raipur',
      district: 'Raipur',
      dailyCapacityQuintals: 1500,
      operatingHours: '08:00 AM - 05:00 PM'
    },
    {
      id: 'CTR_002',
      code: 'KPC-02',
      name: 'Kisan Krishi Upaj Mandi Samiti',
      address: 'NH-53, Mandir Hasaud',
      district: 'Raipur',
      dailyCapacityQuintals: 1200,
      operatingHours: '08:30 AM - 04:30 PM'
    }
  ]);
});

// 3. Crops & MSP Endpoint
app.get('/api/crops', (req, res) => {
  res.json([
    { id: 'CROP_PADDY_COMMON', name: 'Paddy (Common / Dhan)', mspRatePerQuintal: 2300, acceptableMoistureMaxPct: 17.0 },
    { id: 'CROP_PADDY_GRADE_A', name: 'Paddy (Grade A)', mspRatePerQuintal: 2320, acceptableMoistureMaxPct: 17.0 },
    { id: 'CROP_WHEAT', name: 'Wheat (Gehun)', mspRatePerQuintal: 2275, acceptableMoistureMaxPct: 12.0 }
  ]);
});

// 4. Slot Booking (Atomic Token Generation)
app.post('/api/bookings/create', (req, res) => {
  const { farmerId, farmerName, centerId, cropId, expectedQuantity, date, timeSlot } = req.body;
  const sequence = Math.floor(Math.random() * 20) + 1;
  const bookingId = `BK20260826${String(sequence).padStart(4, '0')}`;
  const tokenNumber = `A-${100 + sequence}`;

  res.json({
    bookingId,
    tokenNumber,
    date,
    timeSlot,
    status: 'CONFIRMED',
    qrPayload: JSON.stringify({ id: bookingId, token: tokenNumber, farmer: farmerName })
  });
});

// 5. Live Queue Position
app.get('/api/queue/:centerId', (req, res) => {
  res.json({
    centerId: req.params.centerId,
    nowServing: 'A-118',
    totalWaiting: 14,
    estimatedTurnaroundMinutesPerFarmer: 7,
    items: [
      { token: 'A-118', status: 'CALLED' },
      { token: 'A-119', status: 'WAITING' },
      { token: 'A-120', status: 'WAITING' },
      { token: 'A-124', status: 'WAITING' }
    ]
  });
});

// 6. Complete Procurement
app.post('/api/procurement/complete', (req, res) => {
  const { bookingId, netWeight, ratePerQuintal, grade } = req.body;
  const totalAmount = (netWeight || 50) * (ratePerQuintal || 2300);
  res.json({
    procurementRecordId: `PR_${bookingId}`,
    receiptNumber: `REC-20260826-${bookingId}`,
    totalAmount,
    paymentStatus: 'PROCESSING',
    transactionRef: `TXN${Math.floor(100000 + Math.random() * 900000)}`
  });
});

app.listen(PORT, () => {
  console.log(`🚀 Smart Farmer Procurement Server running on http://localhost:${PORT}`);
});
