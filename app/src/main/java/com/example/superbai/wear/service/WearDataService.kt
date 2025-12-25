package com.example.superbai.wear.service

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.superbai.wear.data.BookingData
import com.google.android.gms.wearable.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.android.gms.tasks.Tasks

class WearDataService(private val context: Context) {
    
    private val dataClient: DataClient = Wearable.getDataClient(context)
    private val messageClient: MessageClient = Wearable.getMessageClient(context)
    
    private val _bookings = MutableStateFlow<List<BookingData>>(emptyList())
    val bookings: StateFlow<List<BookingData>> = _bookings.asStateFlow()
    
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    
    companion object {
        private const val TAG = "WearDataService"
        private const val BOOKING_DATA_PATH = "/superbai/bookings"
        private const val REQUEST_DATA_PATH = "/superbai/request_data"
        private const val DATA_KEY = "bookings_json"
    }
    
    init {
        setupDataListener()
        checkConnection()
    }
    
    private fun setupDataListener() {
        dataClient.addListener { dataEvents ->
            dataEvents.forEach { event ->
                if (event.type == DataEvent.TYPE_CHANGED && 
                    event.dataItem.uri.path == BOOKING_DATA_PATH) {
                    
                    val dataItem = event.dataItem
                    DataMapItem.fromDataItem(dataItem).dataMap.apply {
                        val bookingsJson = getString(DATA_KEY)
                        if (bookingsJson != null) {
                            Log.d(TAG, "Received booking data from phone")
                            val bookingsList = BookingData.listFromJson(bookingsJson)
                            _bookings.value = bookingsList
                        }
                    }
                }
            }
        }
    }
    
    private fun checkConnection() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val nodes = Tasks.await(Wearable.getNodeClient(context).connectedNodes)
                _isConnected.value = nodes.isNotEmpty()
                Log.d(TAG, "Connected nodes: ${nodes.size}")
            } catch (e: Exception) {
                Log.e(TAG, "Error checking connection", e)
                _isConnected.value = false
            }
        }
    }
    
    // Request fresh data from phone
    fun requestDataFromPhone() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val nodes = Tasks.await(Wearable.getNodeClient(context).connectedNodes)
                nodes.forEach { node ->
                    Tasks.await(messageClient.sendMessage(
                        node.id,
                        REQUEST_DATA_PATH,
                        byteArrayOf()
                    ))
                    Log.d(TAG, "Sent data request to phone: ${node.displayName}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error requesting data from phone", e)
            }
        }
    }
    
    // Get cached bookings from DataLayer
    fun loadCachedBookings() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dataItems = Tasks.await(dataClient.getDataItems(
                    Uri.parse("wear://*$BOOKING_DATA_PATH")
                ))
                
                dataItems.forEach { dataItem ->
                    DataMapItem.fromDataItem(dataItem).dataMap.apply {
                        val bookingsJson = getString(DATA_KEY)
                        if (bookingsJson != null) {
                            val bookingsList = BookingData.listFromJson(bookingsJson)
                            _bookings.value = bookingsList
                            Log.d(TAG, "Loaded ${bookingsList.size} cached bookings")
                        }
                    }
                }
                dataItems.release()
            } catch (e: Exception) {
                Log.e(TAG, "Error loading cached bookings", e)
            }
        }
    }
}
