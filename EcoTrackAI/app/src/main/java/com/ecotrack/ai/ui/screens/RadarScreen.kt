package com.ecotrack.ai.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ecotrack.ai.ui.components.CyberButton
import com.ecotrack.ai.ui.theme.CyberNeonGreen

@Composable
fun RadarScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "PROXIMITY SCANNER",
            color = CyberNeonGreen,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        
        Text(
            text = "Status: SCANNING",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
        
        Spacer(modifier = Modifier.height(64.dp))
        
        // Radar Canvas Animation Placeholder
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(300.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = center
                val maxRadius = size.minDimension / 2
                
                // Draw concentric circles
                for (i in 1..4) {
                    drawCircle(
                        color = CyberNeonGreen.copy(alpha = 0.2f * i),
                        radius = maxRadius * (i / 4f),
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
            
            Text(
                text = "WARM\nEst. 12m",
                color = CyberNeonGreen,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        CyberButton(
            text = "Stop Scan & Return",
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
