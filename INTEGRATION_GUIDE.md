# Superbai Watch - Phone Integration Setup Guide

## Overview
This guide will help you connect your Wear OS watch app with the Flutter mobile app to display real-time booking and maid details on the watch.

---

## STEP 1: Build and Install Updated Watch App

### 1.1 Clean and Build Watch App
```powershell
cd R:\Ruchi\Letmedo\Projex\SuperbaiWatch
./gradlew clean
./gradlew assembleDebug
```

### 1.2 Install on Watch/Emulator
```powershell
./gradlew installDebug
```

---

## STEP 2: Update Flutter Mobile App

### 2.1 Install Flutter Dependencies
```powershell
cd R:\Ruchi\Letmedo\Projex\SuperbaiWatch\SuperbaiApp
flutter pub get
```

### 2.2 Update main.dart to Initialize Wear Sync

Add this import at the top of `lib/main.dart`:
```dart
import 'package:superbai/wear_sync_service.dart';
```

In your `main()` function or `initState()` of your root widget, add:
```dart
// Initialize Wear OS sync
WearSyncService.initialize();
```

Example:
```dart
void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();
  
  // Initialize Wear OS sync
  WearSyncService.initialize();
  
  runApp(const MyApp());
}
```

### 2.3 Update booking_screen.dart to Sync Data

Add this import at the top:
```dart
import 'package:superbai/wear_sync_service.dart';
```

In the `_processAndSetBookings` method around line 240-260, add this line AFTER setting the state:
```dart
// Inside _processAndSetBookings method, after setState
if (mounted) {
  setState(() {
    _activeBookings = active;
    _instantBookings = instant;
    _previousBookings = previous;
  });
  
  // NEW: Sync active bookings to watch
  WearSyncService.syncBookingsToWatch(active);
}
```

---

## STEP 3: Build and Install Flutter App

### 3.1 Build APK
```powershell
cd R:\Ruchi\Letmedo\Projex\SuperbaiWatch\SuperbaiApp
flutter build apk --debug
```

### 3.2 Install on Phone
```powershell
flutter install
```

Or manually:
```powershell
adb install build/app/outputs/flutter-apk/app-debug.apk
```

---

## STEP 4: Pair Watch with Phone

### For Real Device:
1. Install **Wear OS app** on your phone from Play Store
2. Open Wear OS app and follow pairing instructions
3. Make sure both devices are on same WiFi/Bluetooth

### For Emulator:
1. Start Wear OS emulator first
2. Use ADB to forward ports:
```powershell
adb -d forward tcp:5601 tcp:5601
```

---

## STEP 5: Test the Connection

### 5.1 Open Mobile App
1. Open Superbai app on phone
2. Login with your account
3. Go to Bookings screen
4. You should see bookings listed

### 5.2 Open Watch App  
1. Open Superbai app on watch
2. After splash screen, you should see:
   - "SUPERBAI" header
   - Your active bookings with:
     - Service name (Cleaning, Cooking, etc.)
     - Status (Active, In Progress, etc.)
     - Maid name
     - Time slot
     - Booking date
3. Tap "🔄 Refresh Data" button to manually sync

### 5.3 Verify Data Sync
- Add/modify a booking in mobile app
- Pull down to refresh in booking screen
- Check watch app - it should update automatically
- Or tap refresh button on watch

---

## STEP 6: Debugging & Troubleshooting

### Check Watch Logs
```powershell
adb logcat | Select-String "WearDataService|superbai"
```

### Check Phone Logs  
```powershell
adb -d logcat | Select-String "WearSync|Synced"
```

### Common Issues:

**Issue: "Phone Disconnected" shown on watch**
- Solution: Make sure phone and watch are paired via Wear OS app
- For emulator: Run `adb -d forward tcp:5601 tcp:5601`

**Issue: No bookings appear on watch**
- Solution: 
  1. Open mobile app and go to bookings screen
  2. Make sure you have active bookings
  3. Tap refresh button on watch
  4. Check logs for sync messages

**Issue: Data not updating**
- Solution:
  1. Force close both apps
  2. Restart both apps
  3. Check Firebase connection on mobile app

---

## STEP 7: Data Flow Architecture

```
[Flutter Mobile App]
        ↓
  User Bookings Fetched from Firebase
        ↓
  WearSyncService converts to watch format
        ↓
  Data sent via Wear DataLayer API
        ↓
  [Wear OS Watch App]
        ↓
  WearDataService receives data
        ↓
  UI updates with real booking info
```

---

## STEP 8: What Data is Synced

The watch receives:
- ✅ Service name (Cleaning, Cooking, etc.)
- ✅ Booking status (Active, In Progress, Completed)
- ✅ Maid name
- ✅ Maid ID
- ✅ Time slot (e.g., "9:00 - 12:00")
- ✅ Booking date
- ✅ Today's status

Only **active/in-progress bookings** are synced (max 5 recent bookings).
Cancelled and completed bookings are filtered out.

---

## STEP 9: Battery Optimization

The watch app includes:
- ✅ Automatic screen sleep
- ✅ Efficient vector graphics
- ✅ Minimal memory usage
- ✅ Cached data support
- ✅ Background sync only when needed

---

## STEP 10: Testing Checklist

- [ ] Watch app installs successfully
- [ ] Mobile app installs successfully  
- [ ] Watch and phone are paired (or emulator port forwarded)
- [ ] Login on mobile app works
- [ ] Bookings show in mobile app
- [ ] Watch app shows splash screen
- [ ] Watch app displays bookings from phone
- [ ] Refresh button works on watch
- [ ] Connection status shows correctly
- [ ] New bookings appear on watch after mobile app refresh

---

## Support & Next Steps

### Current Features:
✅ Real-time booking sync from phone to watch
✅ Display service, maid, time slot details
✅ Multiple bookings support
✅ Manual refresh capability
✅ Connection status indicator

### Future Enhancements:
- 🔄 Two-way communication (send actions from watch to phone)
- 📞 Emergency alert button
- ⏱️ Service start/end tracking
- 📊 Attendance marking
- 🔔 Push notifications

---

## Quick Commands Reference

```powershell
# Build watch app
cd R:\Ruchi\Letmedo\Projex\SuperbaiWatch
./gradlew installDebug

# Build mobile app
cd SuperbaiApp
flutter pub get
flutter build apk --debug
flutter install

# Port forward for emulator
adb -d forward tcp:5601 tcp:5601

# Launch watch app
adb shell am start -n com.example.superbai.wear/com.example.superbai.wear.presentation.MainActivity

# View logs
adb logcat | Select-String "superbai"
```

---

**Note**: Make sure both watch and phone apps are running simultaneously for data sync to work!
