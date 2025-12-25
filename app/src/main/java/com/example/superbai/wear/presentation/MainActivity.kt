package com.example.superbai.wear.presentation

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.wear.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.background
import kotlinx.coroutines.delay
import com.example.superbai.wear.R
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import com.example.superbai.wear.service.WearDataService
import com.example.superbai.wear.data.BookingData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.compose.ui.text.style.TextAlign

class MainActivity : ComponentActivity() {
    private var showSplash = mutableStateOf(true)
    private lateinit var wearDataService: WearDataService
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Wear Data Service
        wearDataService = WearDataService(this)
        
        // Battery optimization: Clear FLAG_KEEP_SCREEN_ON to allow screen to sleep
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Battery optimization: Lifecycle observer to pause when not visible
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                // App goes to background, system handles power management
            }
            
            override fun onResume(owner: LifecycleOwner) {
                // Request fresh data when app comes to foreground
                wearDataService.requestDataFromPhone()
            }
        })
        
        // Load cached data and request fresh data from phone
        wearDataService.loadCachedBookings()
        wearDataService.requestDataFromPhone()
        
        setContent {
            // Use the state from the class level
            if (showSplash.value) {
                SplashScreen()
            } else {
                SuperbaiWatchApp(wearDataService)
            }
        }
        
        // Hide splash screen after delay
        window.decorView.postDelayed({
            showSplash.value = false
        }, 2500)
    }
}

@Composable
fun SplashScreen() {
    val context = LocalContext.current
    val logoBitmap = remember {
        // Load bitmap with proper sampling to avoid out of memory
        val options = BitmapFactory.Options().apply {
            inSampleSize = 4 // Scale down by 4x to reduce memory usage
            inPreferredConfig = Bitmap.Config.RGB_565 // Use less memory per pixel
        }
        BitmapFactory.decodeResource(context.resources, R.drawable.ic_superbai_logo, options)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        logoBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Superbai Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun SuperbaiWatchApp(wearDataService: WearDataService) {
    val bookings by wearDataService.bookings.collectAsState()
    val isConnected by wearDataService.isConnected.collectAsState()
    
    Scaffold(
        timeText = { TimeText() }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 20.dp)
        ) {
            // Header
            item {
                Text(
                    text = "SUPERBAI",
                    style = MaterialTheme.typography.title3,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
            
            // Connection Status
            if (!isConnected) {
                item {
                    Text(
                        text = "📱 Phone Disconnected",
                        style = MaterialTheme.typography.caption1,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }

            // Display bookings or empty state
            if (bookings.isEmpty()) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "No Active Bookings",
                            style = MaterialTheme.typography.body2,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { wearDataService.requestDataFromPhone() },
                            colors = ButtonDefaults.primaryButtonColors()
                        ) {
                            Text("🔄 Refresh")
                        }
                    }
                }
            } else {
                // Display each booking
                bookings.forEachIndexed { index, booking ->
                    item {
                        BookingCard(booking)
                    }
                    if (index < bookings.size - 1) {
                        item { Spacer(modifier = Modifier.height(12.dp)) }
                    }
                }
                
                // Refresh button at bottom
                item { Spacer(modifier = Modifier.height(12.dp)) }
                item {
                    Button(
                        onClick = { wearDataService.requestDataFromPhone() },
                        colors = ButtonDefaults.secondaryButtonColors()
                    ) {
                        Text("🔄 Refresh Data")
                    }
                }
            }
        }
    }
}

@Composable
fun BookingCard(booking: BookingData) {
    Card(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Service Name
            Text(
                text = booking.serviceName,
                style = MaterialTheme.typography.title3,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            
            // Status
            val statusColor = when (booking.status) {
                "In Progress", "Active" -> Color(0xFF4CAF50)
                "Completed" -> Color(0xFF2196F3)
                "Pending" -> Color(0xFFFFA726)
                else -> Color.Gray
            }
            
            Text(
                text = booking.status,
                color = statusColor,
                style = MaterialTheme.typography.caption1,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Maid Name
            Text(
                text = "👤 ${booking.maidName}",
                style = MaterialTheme.typography.body2,
                color = Color.White
            )
            
            // Time Slot
            Text(
                text = "🕐 ${booking.timeSlot}",
                style = MaterialTheme.typography.body2,
                color = Color.White
            )
            
            // Booking Date
            Text(
                text = "📅 ${booking.bookingDate}",
                style = MaterialTheme.typography.caption1,
                color = Color.Gray
            )
            
            if (booking.todayStatus.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Today: ${booking.todayStatus}",
                    style = MaterialTheme.typography.caption1,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}
