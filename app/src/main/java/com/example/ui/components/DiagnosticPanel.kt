package com.example.ui.components

import java.util.Locale
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun DiagnosticPanel(
    reactorStability: Float,
    reactorTemp: Float,
    satelliteStrength: Int,
    batteryLevel: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Core diagnostic header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SYSTEM TELEMETRY // MARK 85",
                color = JarvisPrimaryGlow,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "CORES ACTIVE",
                color = JarvisSuccess,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Reactor Stability Card
            TelemetryCard(
                title = "CORE STABILITY",
                value = String.format(Locale.getDefault(), "%.1f%%", reactorStability),
                percentage = (reactorStability - 90f) / 15f,
                glowColor = JarvisPrimaryGlow,
                modifier = Modifier.weight(1f)
            )

            // Core Temp Card (Warn color if too hot)
            val tempColor = if (reactorTemp > 39.5f) JarvisSecondaryGlow else JarvisSuccess
            TelemetryCard(
                title = "CORE TEMPERATURE",
                value = String.format(Locale.getDefault(), "%.1f °C", reactorTemp),
                percentage = reactorTemp / 50f,
                glowColor = tempColor,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Satellite Strength Card
            TelemetryCard(
                title = "SATELLITE SYNC",
                value = "$satelliteStrength% LNK",
                percentage = satelliteStrength / 100f,
                glowColor = JarvisPrimaryGlow,
                modifier = Modifier.weight(1f)
            )

            // Actual device battery (Real hardware level!)
            TelemetryCard(
                title = "HOST POWER SOURCE",
                value = "$batteryLevel% BAT",
                percentage = batteryLevel / 100f,
                glowColor = if (batteryLevel < 25) JarvisSecondaryGlow else JarvisSuccess,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TelemetryCard(
    title: String,
    value: String,
    percentage: Float,
    glowColor: Color,
    modifier: Modifier = Modifier
) {
    val progressAnimated by animateFloatAsState(targetValue = percentage.coerceIn(0f, 1f))

    Box(
        modifier = modifier
            .background(JarvisSurface.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, JarvisBorder), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                color = JarvisTextSecondary,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = value,
                color = JarvisTextPrimary,
                fontSize = 18.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.W900
            )

            // Tech linear telemetry bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Color(0x1100E5FF), RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressAnimated)
                        .background(
                            Brush.horizontalGradient(listOf(glowColor.copy(alpha = 0.5f), glowColor)),
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    }
}
