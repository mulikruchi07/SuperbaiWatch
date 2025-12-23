package com.example.superbai.wear.presentation

import android.os.Bundle
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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        
        // Keep the splash screen visible for 2 seconds
        splashScreen.setKeepOnScreenCondition { false }
        
    super.onCreate(savedInstanceState)

    setContent {
        SuperbaiWatchApp()
    }
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
