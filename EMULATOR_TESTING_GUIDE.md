# 🚀 Superbai Mobile + Watch App Integration - Testing Guide

## Current Setup
✅ Watch App: Running on Wear OS Emulator (emulator-5554)
✅ Mobile App: Running on same Emulator (emulator-5554)
✅ Port Forwarding: Enabled (tcp:5601)

---

## How to Test Data Sync

### Option 1: Automated Testing (RECOMMENDED)

#### Step 1: Ensure Both Apps Are Installed
```powershell
# Mobile app is already installed
adb shell pm list packages | Select-String "superbai"
# Output should show:
# package:com.example.superbai
# package:com.example.superbai.wear
```

#### Step 2: Enable Port Forwarding
```powershell
adb forward tcp:5601 tcp:5601
# Output: 5601
```

#### Step 3: Launch Both Apps
```powershell
# Launch mobile app
adb shell am start -n com.example.superbai/.MainActivity

# Wait 2 seconds
Start-Sleep -Seconds 2

# Launch watch app
adb shell am start -n com.example.superbai.wear/com.example.superbai.wear.presentation.MainActivity
```

#### Step 4: Login & View Bookings
1. On mobile app: Login with your account
2. Go to Bookings screen
3. View your active bookings

#### Step 5: Check Watch App
1. Watch app will show splash screen for 2.5 seconds
2. After splash screen, you should see:
   - "SUPERBAI" header
   - **Your real bookings from the phone app** (if you have active bookings)
   - Service name, maid name, time slot, status
   - Refresh button

---

## Testing Scenarios

### Scenario 1: No Bookings
**Expected:** Watch shows "No Active Bookings" with refresh button

### Scenario 2: With Active Bookings
**Steps:**
1. Mobile: Go to Bookings → Active tab
2. Wait for bookings to load
3. Watch: Should display all active bookings

### Scenario 3: Manual Refresh
**Steps:**
1. Add new booking on mobile app
2. On watch: Tap "🔄 Refresh Data" button
3. Watch should update with new booking

### Scenario 4: Connection Status
**Expected:** 
- If connected: Watch shows bookings
- If disconnected: Watch shows "📱 Phone Disconnected"

---

## Troubleshooting

### Issue: Watch shows "No Active Bookings"
**Solutions:**
1. Check mobile app is logged in
2. Go to Bookings screen on mobile
3. Ensure you have active bookings (Status = "Active" or "In Progress")
4. Tap refresh on watch

### Issue: Watch shows "Phone Disconnected"
**Solutions:**
1. Run: `adb forward tcp:5601 tcp:5601`
2. Ensure both apps are running
3. Check logs:
   ```powershell
   adb logcat | Select-String "WearDataService|WearSync"
   ```

### Issue: Mobile App Crashes on Bookings Screen
**Solutions:**
1. Check Firebase connection
2. Make sure user is authenticated
3. Check logs:
   ```powershell
   adb logcat | Select-String "com.example.superbai"
   ```

---

## Data Flow in Emulator Setup

```
[Mobile App (Emulator)]
        ↓ Firebase Query
    Fetch User Bookings
        ↓ WearSyncService
    Convert to JSON
        ↓ Wear DataLayer API
    Send via port 5601
        ↓
[Watch App (Emulator)]
        ↓
    WearDataService receives
        ↓
    Parse JSON
        ↓
    Update UI with bookings
```

---

## Quick Commands Reference

```powershell
# Install mobile app
adb install -r build/app/outputs/flutter-apk/app-debug.apk

# Enable wear data layer
adb forward tcp:5601 tcp:5601

# Launch mobile app
adb shell am start -n com.example.superbai/.MainActivity

# Launch watch app
adb shell am start -n com.example.superbai.wear/com.example.superbai.wear.presentation.MainActivity

# Check app logs
adb logcat | Select-String "superbai"

# List installed apps
adb shell pm list packages | Select-String "superbai"

# Clear app data (if needed)
adb shell pm clear com.example.superbai
adb shell pm clear com.example.superbai.wear
```

---

## Expected Final Result

✅ Mobile app running on emulator
✅ Watch app running on emulator
✅ Both apps visible and responsive
✅ When mobile app shows bookings, watch displays the same data
✅ Refresh button on watch works
✅ Real-time sync between phone and watch

---

## Real Device Setup (When Ready)

When you have both physical devices:
1. Install Wear OS app from Play Store on phone
2. Pair phone + watch via Wear OS app
3. Install mobile app on phone
4. Install watch app on watch
5. Data will sync automatically via Bluetooth/WiFi

No need for `adb forward` on real devices!

---

## Test Checklist

- [ ] Mobile app installed on emulator
- [ ] Watch app installed on emulator
- [ ] Port forwarding enabled (5601)
- [ ] Mobile app can login successfully
- [ ] Mobile app shows bookings in Bookings screen
- [ ] Watch app launches after splash screen
- [ ] Watch app shows booking data from phone
- [ ] Watch refresh button works
- [ ] Connection status indicator shows correctly

✅ **All tests pass? You have working watch-phone integration!**
