package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JarvisPrimaryGlow
import com.example.ui.theme.JarvisSecondaryGlow
import com.example.ui.theme.JarvisSurface
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ArcReactor(
    isSpeaking: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "arc_reactor_rotations")

    // Slow clockwise rotation
    val rotationAngleSlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteTransitionSpec(25000),
        label = "slow_rot"
    )

    // Fast counter-clockwise rotation
    val rotationAngleFast by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteTransitionSpec(8000),
        label = "fast_rot"
    )

    // Speaking or Loading pulsing scale
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isSpeaking) 400 else 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Core glow intensity
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isLoading) 500 else 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Box(
        modifier = modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        // Holographic Background grid rings
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = (size.minDimension / 2) * pulseScale

            // Draw outer radial halo glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        JarvisPrimaryGlow.copy(alpha = 0.25f * glowAlpha),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 1.2f
                ),
                radius = radius * 1.2f,
                center = center
            )

            // Outer thick tech border ring
            drawCircle(
                color = JarvisPrimaryGlow.copy(alpha = 0.3f),
                radius = radius * 0.95f,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Outer dashed ring (Rotating Fast)
            rotate(rotationAngleFast, pivot = center) {
                val numDashes = 24
                val dashLength = 6f
                val gapLength = 10f
                val dashRadius = radius * 0.88f
                for (i in 0 until numDashes) {
                    val angle = (i * (360f / numDashes)) * (Math.PI / 180f)
                    val x = center.x + dashRadius * cos(angle).toFloat()
                    val y = center.y + dashRadius * sin(angle).toFloat()
                    drawCircle(
                        color = JarvisPrimaryGlow.copy(alpha = 0.7f),
                        radius = 3f,
                        center = Offset(x, y)
                    )
                }
            }

            // Inner Rotating arc brackets (Rotating Slow)
            rotate(rotationAngleSlow, pivot = center) {
                val bracketRadius = radius * 0.75f
                // Three large sweep brackets making Tony's Arc Reactor segment rings
                for (i in 0..2) {
                    drawArc(
                        color = JarvisPrimaryGlow,
                        startAngle = i * 120f + 10f,
                        sweepAngle = 90f,
                        useCenter = false,
                        topLeft = Offset(center.x - bracketRadius, center.y - bracketRadius),
                        size = Size(bracketRadius * 2, bracketRadius * 2),
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Node anchors inside reactor
                val corePins = 6
                for (i in 0 until corePins) {
                    val angle = (i * (360f / corePins)) * (Math.PI / 180f)
                    val pinRadius = radius * 0.58f
                    val rx = center.x + pinRadius * cos(angle).toFloat()
                    val ry = center.y + pinRadius * sin(angle).toFloat()
                    drawCircle(
                        color = if (isSpeaking) JarvisSecondaryGlow else JarvisPrimaryGlow,
                        radius = 6f,
                        center = Offset(rx, ry)
                    )
                }
            }

            // Central power core orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (isSpeaking) JarvisSecondaryGlow.copy(alpha = 0.8f) else JarvisPrimaryGlow.copy(alpha = 0.8f),
                        if (isSpeaking) JarvisSecondaryGlow.copy(alpha = 0.2f) else JarvisPrimaryGlow.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 0.45f
                ),
                radius = radius * 0.45f,
                center = center
            )

            // Central solid core ring
            drawCircle(
                color = if (isSpeaking) JarvisSecondaryGlow else JarvisPrimaryGlow,
                radius = radius * 0.35f,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // Animated Digital reading overlay text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isLoading) "SYNCING..." else if (isSpeaking) "SPEAKING" else "J.A.R.V.I.S.",
                color = if (isSpeaking) JarvisSecondaryGlow else JarvisPrimaryGlow,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )
            Text(
                text = "${(pulseScale * 100).toInt()}%",
                color = JarvisPrimaryGlow.copy(alpha = 0.8f),
                fontSize = 24.sp,
                fontWeight = FontWeight.W900,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )
            Text(
                text = "STARK IND.",
                color = JarvisPrimaryGlow.copy(alpha = 0.5f),
                fontSize = 8.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )
        }

        // Floating Sleek HUD Data Badges around the core
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 8.dp, y = (-4).dp)
                .background(JarvisSurface.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                .border(BorderStroke(1.dp, JarvisPrimaryGlow.copy(alpha = 0.3f)), RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "CPU: ${(10 + (pulseScale * 5).toInt())}%",
                color = JarvisPrimaryGlow,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-14).dp, y = 0.dp)
                .background(JarvisSurface.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                .border(BorderStroke(1.dp, JarvisPrimaryGlow.copy(alpha = 0.3f)), RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "TEMP: 34°C",
                color = JarvisPrimaryGlow,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 8.dp, y = 4.dp)
                .background(JarvisSurface.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                .border(BorderStroke(1.dp, JarvisPrimaryGlow.copy(alpha = 0.3f)), RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "LAT: 0.002ms",
                color = JarvisPrimaryGlow,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

private fun infiniteTransitionSpec(duration: Int): InfiniteRepeatableSpec<Float> {
    return infiniteRepeatable(
        animation = tween(durationMillis = duration, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
    )
}
