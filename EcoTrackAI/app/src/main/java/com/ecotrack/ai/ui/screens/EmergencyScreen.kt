package com.ecotrack.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ecotrack.ai.ui.components.CyberButton
import com.ecotrack.ai.ui.theme.CyberAlertRed
import com.ecotrack.ai.ui.theme.CyberBlack

@Composable
fun EmergencyScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBlack)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "EMERGENCY GUARDIAN",
            color = CyberAlertRed,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Activating this mode will instantly transmit your GPS location, capture silent photos, and sound an SOS alarm.",
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        
        Spacer(modifier = Modifier.height(64.dp))
        
        CyberButton(
            text = "TRIGGER SOS ALARM",
            color = CyberAlertRed,
            onClick = { /* TODO: Trigger Service */ },
            modifier = Modifier.fillMaxWidth().height(80.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        CyberButton(
            text = "Cancel",
            color = MaterialTheme.colorScheme.onSurface,
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
