import 'dart:convert';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';

class WearSyncService {
  static const String _bookingDataPath = '/superbai/bookings';
  static const String _requestDataPath = '/superbai/request_data';
  static const String _dataKey = 'bookings_json';

  // Sync current user's bookings to watch
  static Future<void> syncBookingsToWatch(
    List<Map<String, dynamic>> bookings,
  ) async {
    try {
      // Convert bookings to simplified format for watch
      final watchBookings = bookings.map((booking) {
        return {
          'id': booking['id'] ?? '',
          'serviceName': booking['service'] ?? 'Unknown Service',
          'status': booking['Status'] ?? 'Unknown',
          'maidName': booking['name'] ?? 'Maid Not Assigned',
          'maidId': booking['maidId'] ?? '',
          'timeSlot': booking['timing'] ?? 'Not Set',
          'bookingDate': _formatDate(booking['BookingDate']),
          'todayStatus': booking['todayStatus'] ?? 'Not Started',
        };
      }).toList();

      // Convert to JSON
      final bookingsJson = jsonEncode(watchBookings);

      // Send to watch using DataLayer
      await WearDataLayer.sendData(_bookingDataPath, {_dataKey: bookingsJson});

      print('✅ Synced ${watchBookings.length} bookings to watch');
    } catch (e) {
      print('❌ Error syncing to watch: $e');
    }
  }

  // Listen for data requests from watch
  static void listenForWatchRequests() {
    WearDataLayer.listenForMessages(_requestDataPath).listen((message) {
      print('📱 Watch requested data refresh');
      // Fetch and sync latest bookings
      _fetchAndSyncBookings();
    });
  }

  // Fetch current user's active bookings and sync to watch
  static Future<void> _fetchAndSyncBookings() async {
    try {
      final user = FirebaseAuth.instance.currentUser;
      if (user == null) return;

      // Fetch active bookings
      final snapshot = await FirebaseFirestore.instance
          .collection('FACT_BOOKINGS')
          .where('UserID', isEqualTo: user.uid)
          .where('Status', whereIn: ['Active', 'In Progress', 'Pending'])
          .limit(5) // Limit to 5 most recent bookings for watch
          .get();

      if (snapshot.docs.isEmpty) {
        await syncBookingsToWatch([]);
        return;
      }

      // Process bookings data
      List<Map<String, dynamic>> bookingsData = [];
      for (var doc in snapshot.docs) {
        final data = doc.data();
        data['id'] = doc.id;

        // Fetch related service and timeslot info
        if (data['ServiceID'] != null) {
          final serviceDoc = await FirebaseFirestore.instance
              .collection('DIM_SERVICES')
              .doc(data['ServiceID'])
              .get();
          data['service'] = serviceDoc.data()?['ServiceName'] ?? 'Unknown';
        }

        if (data['TimeSlotID'] != null) {
          final timeSlotDoc = await FirebaseFirestore.instance
              .collection('DIM_TIME_SLOTS')
              .doc(data['TimeSlotID'])
              .get();
          data['timing'] = timeSlotDoc.data()?['TimeSlots'] ?? 'Not Set';
        }

        // Add maid name (placeholder - update based on your data structure)
        if (data['MaidID'] != null && data['MaidID'] != 'N/A') {
          // Fetch maid name from your maids collection
          data['name'] = 'Maid'; // Placeholder
        } else {
          data['name'] = 'Not Assigned';
        }

        bookingsData.add(data);
      }

      await syncBookingsToWatch(bookingsData);
    } catch (e) {
      print('❌ Error fetching bookings for watch: $e');
    }
  }

  static String _formatDate(dynamic timestamp) {
    if (timestamp == null) return 'Not Set';
    try {
      if (timestamp is Timestamp) {
        final date = timestamp.toDate();
        return '${date.day}/${date.month}/${date.year}';
      }
      return 'Not Set';
    } catch (e) {
      return 'Not Set';
    }
  }

  // Initialize watch sync on app start
  static void initialize() {
    listenForWatchRequests();
    _fetchAndSyncBookings(); // Initial sync
  }
}

// Minimal no-op DataLayer shim so Flutter build succeeds on mobile.
// Replace with a real implementation (e.g., method channel to Android Wear Data Layer)
// when you are ready to send data to the watch from Flutter.
class WearDataLayer {
  static Future<void> sendData(String path, Map<String, String> data) async {
    // TODO: implement platform channel to send data to Wear OS
  }

  static Stream<String> listenForMessages(String path) {
    // TODO: implement platform channel listener for watch messages
    return const Stream.empty();
  }
}
