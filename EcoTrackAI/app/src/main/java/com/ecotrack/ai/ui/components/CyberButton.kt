package com.ecotrack.ai.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ecotrack.ai.ui.theme.CyberBlack
import com.ecotrack.ai.ui.theme.CyberNeonGreen

@Composable
fun CyberButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = CyberNeonGreen
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = CutCornerShape(topStart = 12.dp, bottomEnd = 12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CyberBlack,
            contentColor = color
        ),
        border = BorderStroke(1.5.dp, color),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(text = text.uppercase(), style = androidx.compose.material3.MaterialTheme.typography.labelLarge)
    }
}
