package com.ecotrack.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ecotrack.ai.intelligence.BatteryPredictionModel
import com.ecotrack.ai.ui.components.CyberButton
import com.ecotrack.ai.ui.theme.CyberAlertRed
import com.ecotrack.ai.ui.theme.CyberBlue
import com.ecotrack.ai.ui.theme.CyberDarkGray
import com.ecotrack.ai.ui.theme.CyberNeonGreen

@Composable
fun DashboardScreen(navController: NavController) {
    val batteryModel = remember { BatteryPredictionModel() }
    val currentBattery = 85 // Mocked for UI
    val minsLeft = batteryModel.predictTimeUntilShutdown(currentBattery, isPowerSaving = false)
    val batteryText = "$currentBattery% - ${batteryModel.formatPrediction(minsLeft)}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "SYSTEM STATUS",
            color = CyberBlue,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // System Health Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CyberDarkGray),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                StatusRow("AI GUARDIAN", "ACTIVE", CyberNeonGreen)
                Spacer(modifier = Modifier.height(8.dp))
                StatusRow("BATTERY", batteryText, CyberNeonGreen)
                Spacer(modifier = Modifier.height(8.dp))
                StatusRow("THEFT RISK", "LOW (12/100)", CyberBlue)
                Spacer(modifier = Modifier.height(8.dp))
                StatusRow("LAST SYNC", "2 MINS AGO", MaterialTheme.colorScheme.onSurface)
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Actions
        CyberButton(
            text = "Radar Proximity Scan",
            onClick = { navController.navigate("radar") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        CyberButton(
            text = "Emergency Guardian Mode",
            color = CyberAlertRed,
            onClick = { navController.navigate("emergency") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun StatusRow(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        Text(text = value, color = valueColor, fontWeight = FontWeight.Bold)
    }
}
