# Firebase Configuration & Deployment Guide

## 1. Firebase Project Setup

1. Navigate to the [Firebase Console](https://console.firebase.google.com/).
2. Click **Create Project** and name it `smart-farmer-procurement`.
3. Enable **Google Analytics** (optional).

## 2. Register Android Application

1. In Project Overview, click **Add App** $\rightarrow$ **Android**.
2. **Android package name**: `com.smartfarmer.procurement`
3. **App nickname**: `Smart Farmer Procurement`
4. **Debug signing certificate SHA-1**:
   Run the following in your terminal to obtain your SHA-1 fingerprint:
   ```bash
   ./gradlew signingReport
   ```
5. Download `google-services.json` and move it into the `app/` folder:
   ```text
   SmartFarmerProcurement/app/google-services.json
   ```

## 3. Enable Authentication & Firestore

1. **Authentication**:
   - Go to **Build** $\rightarrow$ **Authentication** $\rightarrow$ **Sign-in method**.
   - Enable **Phone** (for OTP authentication) and **Email/Password** or **Anonymous**.
2. **Cloud Firestore**:
   - Go to **Build** $\rightarrow$ **Firestore Database** $\rightarrow$ **Create database**.
   - Select production mode and choose your nearest data center (e.g. `asia-south1` for Mumbai).
3. **Deploy Security Rules & Indexes**:
   Install the Firebase CLI and run:
   ```bash
   npm install -g firebase-tools
   firebase login
   firebase init firestore
   firebase deploy --only firestore:rules,firestore:indexes
   ```

## 4. Run Automated Database Seeding

To pre-fill procurement centres and official MSP crop data into Firestore:
1. Download a service account private key from **Project Settings** $\rightarrow$ **Service accounts**.
2. Place the file as `firestore/serviceAccountKey.json`.
3. Run the seed script:
   ```bash
   cd firestore
   npm install
   node seed.js
   ```

## 5. Enable Firebase Cloud Messaging (FCM)

1. Go to **Project Settings** $\rightarrow$ **Cloud Messaging**.
2. Copy the **Server Key** or **Sender ID** for server-side push notification integration.
