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
import androidx.compose.ui.text.style.TextAlign

class MainActivity : ComponentActivity() {
    private var showSplash = mutableStateOf(true)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Battery optimization: Clear FLAG_KEEP_SCREEN_ON
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                // App paused - system handles power
            }
        })
        
        setContent {
            if (showSplash.value) {
                SplashScreen()
            } else {
                WatchBookingDisplay()
            }
        }
        
        // Hide splash after 2.5 seconds
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
fun WatchBookingDisplay() {
    // This is a simple data receiver - data comes from phone via SharedPreferences
    // stored by the Bluetooth service
    val context = LocalContext.current
    var bookingData by remember { mutableStateOf<Map<String, String>?>(null) }
    var lastUpdate by remember { mutableStateOf("Loading...") }
    
    // Simulate receiving data from phone (in real implementation, 
    // this would be updated by BluetoothService)
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000) // Check every 2 seconds
            // In production, this would read from SharedPreferences 
            // updated by Bluetooth receiver
            bookingData = null // Will be updated when phone sends data
        }
    }
    
    Scaffold(timeText = { TimeText() }) {
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp)
        ) {
            item {
                Text(
                    text = "📱 SUPERBAI",
                    style = MaterialTheme.typography.title3,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
            
            item { Spacer(modifier = Modifier.height(8.dp)) }
            
            if (bookingData == null) {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Waiting for phone...",
                            style = MaterialTheme.typography.body2,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Make sure phone app is running",
                            style = MaterialTheme.typography.caption1,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                item {
                    Card(onClick = {}) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = bookingData!!["service"] ?: "Service",
                                style = MaterialTheme.typography.title3,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Text(
                                text = "👤 ${bookingData!!["maid"]}",
                                style = MaterialTheme.typography.body2,
                                color = Color.White
                            )
                            Text(
                                text = "🕐 ${bookingData!!["time"]}",
                                style = MaterialTheme.typography.body2,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
