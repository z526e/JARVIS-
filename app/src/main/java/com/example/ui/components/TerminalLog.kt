package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JarvisBorder
import com.example.ui.theme.JarvisPrimaryGlow
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisTextPrimary

@Composable
fun TerminalLog(
    logs: List<String>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Auto-scroll logic as new scifi traces are logged
    LaunchedEffect(logs.size) {
        if (logs.isNotEmpty()) {
            listState.animateScrollToItem(logs.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(JarvisSurface.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, JarvisBorder), RoundedCornerShape(16.dp))
            .padding(10.dp)
    ) {
        Text(
            text = "MAIN DIAGNOSTIC CONSOLE // LOGS",
            color = JarvisPrimaryGlow,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        ) {
            if (logs.isEmpty()) {
                Text(
                    text = "No diagnostic traces received...",
                    color = JarvisTextPrimary.copy(alpha = 0.4f),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(logs) { logLine ->
                        val isAlert = logLine.contains("PROTOCOL") || logLine.contains("ERROR")
                        val textColor = if (isAlert) {
                            com.example.ui.theme.JarvisSecondaryGlow
                        } else {
                            JarvisTextPrimary.copy(alpha = 0.85f)
                        }
                        Text(
                            text = logLine,
                            color = textColor,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
