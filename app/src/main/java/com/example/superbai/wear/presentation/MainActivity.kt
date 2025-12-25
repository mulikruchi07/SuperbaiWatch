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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Battery optimization: Clear FLAG_KEEP_SCREEN_ON to allow screen to sleep
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Battery optimization: Lifecycle observer to pause when not visible
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                // App goes to background, system handles power management
            }
        })
        
        setContent {
            var showSplash by remember { mutableStateOf(true) }
            
            LaunchedEffect(Unit) {
                delay(2500)
                showSplash = false
            }
            
            if (showSplash) {
                SplashScreen()
            } else {
                SuperbaiWatchApp()
            }
        }
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Using vector drawable instead of large PNG for efficiency
        Image(
            painter = painterResource(id = R.drawable.ic_logo_splash),
            contentDescription = "Superbai Logo",
            modifier = Modifier.size(120.dp)
        )
    }
}

@Composable
fun SuperbaiWatchApp() {

    // Dummy data – will come from phone later
    val serviceName = "Cleaning"
    val bookingStatus = "In Progress"
    val maidName = "Anita"
    val timeSlot = "9:00 – 12:00"
    val todayStatus = "Present"

    Scaffold(
        timeText = { TimeText() }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Text(
                    text = "SUPERBAI",
                    style = MaterialTheme.typography.title3,
                    fontWeight = FontWeight.Bold
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                Text(
                    text = serviceName,
                    style = MaterialTheme.typography.title2,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                Text(
                    text = bookingStatus,
                    color = Color(0xFF4CAF50),
                    style = MaterialTheme.typography.body2
                )
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }

            item {
                Text(
                    text = "Maid: $maidName",
                    style = MaterialTheme.typography.body2
                )
            }

            item {
                Text(
                    text = "Time: $timeSlot",
                    style = MaterialTheme.typography.body2
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                Chip(
                    onClick = {},
                    label = {
                        Text("Today: $todayStatus")
                    },
                    colors = ChipDefaults.primaryChipColors()
                )
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }

            item {
                Button(
                    onClick = {
                        // Send "START_SERVICE" to phone
                    },
                    colors = ButtonDefaults.primaryButtonColors()
                ) {
                    Text("Start / End")
                }
            }

            item {
                Button(
                    onClick = {
                        // Send "ALERT" to phone
                    },
                    colors = ButtonDefaults.secondaryButtonColors()
                ) {
                    Text("Alert")
                }
            }
        }
    }
}
