# 🚀 QUICK START - Test Your Apps Right Now!

## Current Status
✅ Mobile App (com.example.superbai) - INSTALLED
✅ Watch App (com.example.superbai.wear) - INSTALLED  
✅ Port Forwarding (tcp:5601) - ACTIVE

---

## 1️⃣ Start Testing in 3 Steps

### Step 1: Launch Mobile App
```powershell
adb shell am start -n com.example.superbai/.MainActivity
```

### Step 2: Wait 2 seconds, Launch Watch App
```powershell
Start-Sleep -Seconds 2
adb shell am start -n com.example.superbai.wear/com.example.superbai.wear.presentation.MainActivity
```

### Step 3: View Both Apps
- Mobile: Login with your account
- Mobile: Navigate to "Bookings"
- Watch: Wait for splash screen → See your bookings!

---

## 2️⃣ What Should Happen

| Mobile App | Watch App |
|-----------|-----------|
| Shows your bookings from Firebase | Shows same bookings auto-synced! |
| Login screen first | Splash screen then bookings |
| Bookings tab active | Real data displayed |

---

## 3️⃣ Verify Data Sync

**On Mobile:**
- Go to Bookings section
- See active bookings listed

**On Watch:**
- After splash, see:
  - Service name (e.g., "Cleaning")
  - Maid name
  - Time slot (e.g., "9:00 - 12:00")
  - Status (green for Active)
  - Booking date
  - Refresh button

---

## 4️⃣ Test Refresh

1. On Watch: Tap "🔄 Refresh Data" button
2. Watch sends request to phone
3. Phone fetches latest bookings
4. Watch updates with new data

---

## 5️⃣ Logs & Debugging

### View Sync Logs
```powershell
adb logcat | Select-String "WearDataService"
```

### View Superbai Logs
```powershell
adb logcat | Select-String "superbai"
```

### Check Port Forward Status
```powershell
adb forward --list
# Should show: emulator-5554 tcp:5601 tcp:5601
```

---

## ⚠️ If Something's Not Working

### Watch shows "No Active Bookings"
```powershell
# Make sure:
1. Mobile app is logged in
2. You have active bookings (Status = "Active")
3. Check mobile app bookings screen
```

### Watch shows "Phone Disconnected"
```powershell
# Reconnect the port:
adb forward tcp:5601 tcp:5601
```

### Apps not launching
```powershell
# Verify they're installed:
adb shell pm list packages | Select-String "superbai"

# Reinstall if needed:
adb install -r build/app/outputs/flutter-apk/app-debug.apk
```

---

## 🎯 Success = 

✅ Mobile app shows your bookings
✅ Watch app shows the SAME bookings  
✅ Data syncs automatically
✅ Refresh button works

---

## 📱 Real Device Setup (When Ready)

No code changes needed! Just:

1. Install mobile app on your real phone
2. Install watch app on your real watch
3. Open Wear OS app on phone
4. Pair phone + watch
5. **Data syncs automatically!**

No manual port forwarding needed on real devices.

---

**Go test it now! 🎉**
