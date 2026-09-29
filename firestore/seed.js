/**
 * Automated Firestore Seeder
 * Run: node firestore/seed.js
 */
const admin = require('firebase-admin');
const fs = require('fs');
const path = require('path');

// Ensure SERVICE_ACCOUNT_KEY path is provided or default to local credentials
const serviceAccountPath = process.env.GOOGLE_APPLICATION_CREDENTIALS || path.join(__dirname, 'serviceAccountKey.json');

if (fs.existsSync(serviceAccountPath)) {
  const serviceAccount = require(serviceAccountPath);
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount)
  });
} else {
  console.log('⚡ Initializing Firebase Admin with default project credentials...');
  admin.initializeApp();
}

const db = admin.firestore();

async function seedDatabase() {
  try {
    const rawData = fs.readFileSync(path.join(__dirname, 'seed_data.json'), 'utf8');
    const seedData = JSON.parse(rawData);

    console.log('🌱 Seeding Procurement Centres...');
    for (const center of seedData.procurement_centers) {
      await db.collection('procurement_centers').doc(center.id).set(center, { merge: true });
      console.log(`  ✓ Added center: ${center.name}`);
    }

    console.log('🌱 Seeding Crops & MSP rates...');
    for (const crop of seedData.crops) {
      await db.collection('crops').doc(crop.id).set(crop, { merge: true });
      console.log(`  ✓ Added crop: ${crop.name} (MSP: ₹${crop.mspRatePerQuintal})`);
    }

    console.log('✅ Firestore Database Seeding Completed Successfully!');
  } catch (error) {
    console.error('❌ Error during seeding:', error);
  }
}

seedDatabase();
