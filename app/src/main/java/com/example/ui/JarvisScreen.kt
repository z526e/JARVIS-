package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChatMessage
import com.example.ui.components.ArcReactor
import com.example.ui.components.DiagnosticPanel
import com.example.ui.components.TerminalLog
import com.example.ui.components.SubMatrixPanel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun JarvisScreen(
    viewModel: JarvisViewModel,
    onVoiceInputClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val isSpeaking by viewModel.isSpeaking.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isListening by viewModel.isListening.collectAsStateWithLifecycle()

    // Telemetry bindings
    val stability by viewModel.reactorStability.collectAsStateWithLifecycle()
    val temp by viewModel.reactorTemp.collectAsStateWithLifecycle()
    val satelliteStrength by viewModel.starkSatelliteStrength.collectAsStateWithLifecycle()
    val batteryLevel by viewModel.batteryLevel.collectAsStateWithLifecycle()
    val logs by viewModel.consoleLogs.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisBackground),
        containerColor = JarvisBackground,
        bottomBar = {
            // Apply inset padding for immersive gesture/navigation pill layout safe area
            JarvisBottomNavigation(
                activeTab = currentTab,
                onTabSelected = { viewModel.setActiveTab(it) },
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(JarvisBackground)
        ) {
            // Simulated High-Tech Status Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 12.dp, end = 24.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STARK_SYS_V10.8",
                    color = JarvisTextSecondary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.alpha(0.6f)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(JarvisPrimaryGlow, CircleShape)
                        )
                        Text(
                            text = "SECURED LINK",
                            color = JarvisPrimaryGlow,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "$batteryLevel% PWR",
                        color = JarvisTextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.alpha(0.6f)
                    )
                }
            }

            // Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 2.dp)
            ) {
                val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
                val greetingPhrase = when {
                    hour in 0..11 -> "Good Morning"
                    hour in 12..16 -> "Good Afternoon"
                    hour in 17..21 -> "Good Evening"
                    else -> "System Active"
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$greetingPhrase, ",
                        color = JarvisTextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Light
                    )
                    Text(
                        text = "Sir",
                        color = JarvisPrimaryGlow,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Sir, all analytical cores and defensive matrices are fully synthesized.",
                    color = JarvisTextSecondary,
                    fontSize = 10.9.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 1.dp),
                    lineHeight = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Body content area
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                val isExpanded = maxWidth >= 600.dp

                if (isExpanded) {
                    // Adaptive dual-screen grid layout for large viewport sizes
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Left Panel (Fixed system telemetry displays)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            ArcReactor(
                                isSpeaking = isSpeaking,
                                isLoading = isLoading,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                            DiagnosticPanel(
                                reactorStability = stability,
                                reactorTemp = temp,
                                satelliteStrength = satelliteStrength,
                                batteryLevel = batteryLevel
                            )
                            TerminalLog(logs = logs)
                        }

                        // Divider element
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp)
                                .background(JarvisGridLine)
                        )

                        // Right Panel (Dynamic layout switching based on navigation actions)
                        Column(
                            modifier = Modifier
                                .weight(1.3f)
                                .fillMaxHeight()
                        ) {
                            AnimatedContent(
                                targetState = currentTab,
                                transitionSpec = {
                                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                                },
                                modifier = Modifier.weight(1f),
                                label = "expanded_right_panel"
                            ) { targetTab ->
                                when (targetTab) {
                                    "dashboard" -> {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            QuickActionHub(onActionSelected = { viewModel.triggerProtocol(it) })
                                            
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = JarvisSurface),
                                                border = BorderStroke(1.dp, JarvisBorder),
                                                modifier = Modifier.fillMaxWidth().weight(1f)
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(16.dp),
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text(
                                                        text = "STARK COMMAND DECK OPERATIONS //",
                                                        color = JarvisPrimaryGlow,
                                                        fontSize = 12.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Text(
                                                        text = "Welcome to the central high-fidelity pilot terminal, Sir.\n\n" +
                                                                "- Live telemetry tracking and cooling cycles are active on the left pane.\n" +
                                                                "- Click COGNITIVE ARCH on the navigation deck to activate natural language speech interface.\n" +
                                                                "- Click SUB-MATRIX to configure local Room databases, toggle smart appliances, check PC sync status, or operate Amazon expert research modeling.\n\n" +
                                                                "I am standing by for active instructions.",
                                                        color = JarvisTextSecondary,
                                                        fontSize = 11.5.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        lineHeight = 16.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    "matrix" -> {
                                        SubMatrixPanel(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                                    }
                                    "chat" -> {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            QuickActionHub(onActionSelected = { viewModel.triggerProtocol(it) })
                                            ChatProtocolPane(
                                                viewModel = viewModel,
                                                onVoiceInputClick = onVoiceInputClick,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Mobile portrait single-view layout structure
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                            },
                            modifier = Modifier.weight(1f),
                            label = "mobile_vertical_panel"
                        ) { targetTab ->
                            when (targetTab) {
                                "dashboard" -> {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        ArcReactor(
                                            isSpeaking = isSpeaking,
                                            isLoading = isLoading,
                                            modifier = Modifier.padding(top = 12.dp)
                                        )
                                        DiagnosticPanel(
                                            reactorStability = stability,
                                            reactorTemp = temp,
                                            satelliteStrength = satelliteStrength,
                                            batteryLevel = batteryLevel
                                        )
                                        TerminalLog(
                                            logs = logs,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                }
                                "matrix" -> {
                                    SubMatrixPanel(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                                }
                                "chat" -> {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        QuickActionHub(onActionSelected = { viewModel.triggerProtocol(it) })
                                        ChatProtocolPane(
                                            viewModel = viewModel,
                                            onVoiceInputClick = onVoiceInputClick,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionHub(
    onActionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "STARK SYSTEMS ARCHIVE ACCESS //",
            color = JarvisPrimaryGlow,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onActionSelected("STARK_CORE_OPTIMIZATION") },
                colors = ButtonDefaults.buttonColors(containerColor = JarvisSurface),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, JarvisPrimaryGlow.copy(alpha = 0.5f)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("opt_core_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Cyclone,
                    contentDescription = null,
                    tint = JarvisPrimaryGlow,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "OPTIMIZE CORE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = JarvisTextPrimary
                )
            }

            Button(
                onClick = { onActionSelected("STARK_DEFENSE_GRID") },
                colors = ButtonDefaults.buttonColors(containerColor = JarvisSurface),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, JarvisPrimaryGlow.copy(alpha = 0.5f)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("defense_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = JarvisSuccess,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "DEFENSE GRID",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = JarvisTextPrimary
                )
            }

            Button(
                onClick = { onActionSelected("STARK_CLEAN_MEMORY") },
                colors = ButtonDefaults.buttonColors(containerColor = JarvisSurface),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, JarvisSecondaryGlow.copy(alpha = 0.5f)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier
                    .weight(1.2f)
                    .testTag("clear_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = JarvisSecondaryGlow,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "PURGE CORES",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = JarvisTextPrimary
                )
            }
        }
    }
}

@Composable
fun ChatProtocolPane(
    viewModel: JarvisViewModel,
    onVoiceInputClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val textInput by viewModel.inputText.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isListening by viewModel.isListening.collectAsStateWithLifecycle()

    val chatListState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto-scroll when new messages arrive
    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            chatListState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisSurface.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .border(BorderStroke(1.dp, JarvisGridLine), RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (messages.isEmpty() && !isLoading) {
            // High-tech beautiful empty cold state
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "Empty Archive",
                    tint = JarvisPrimaryGlow.copy(alpha = 0.6f),
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "NEURAL COGNITION SYSTEM SECURE",
                    color = JarvisPrimaryGlow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "আই কার্ভস সিস্টেম অনলাইন, স্যার! আপনার হুকুম করুন।\nSir, I am online and fully functional. Awaiting command parameters.",
                    color = JarvisTextSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        } else {
            // Conversational list
            LazyColumn(
                state = chatListState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(message = msg)
                }

                if (isLoading) {
                    item {
                        JarvisLoadingIndicator()
                    }
                }
            }
        }

        // Input Controls Anchor
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Glowing Microphone Voice Control Action (Launch Recognizer)
            IconButton(
                onClick = onVoiceInputClick,
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        if (isListening) JarvisSecondaryGlow else JarvisGlowSurface,
                        CircleShape
                    )
                    .border(
                        BorderStroke(1.dp, if (isListening) JarvisSecondaryGlow else JarvisPrimaryGlow),
                        CircleShape
                    )
                    .testTag("mic_button")
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Hearing else Icons.Default.Mic,
                    contentDescription = "Voice Input Recognition",
                    tint = if (isListening) JarvisBackground else JarvisPrimaryGlow,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Input TextField
            OutlinedTextField(
                value = textInput,
                onValueChange = { viewModel.onInputTextChange(it) },
                placeholder = {
                    Text(
                        text = "Sir, access command core...",
                        color = JarvisTextSecondary.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                textStyle = LocalTextStyle.current.copy(
                    color = JarvisTextPrimary,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = JarvisPrimaryGlow,
                    unfocusedBorderColor = JarvisGridLine,
                    focusedContainerColor = JarvisSurface.copy(alpha = 0.5f),
                    unfocusedContainerColor = JarvisSurface.copy(alpha = 0.2f),
                    cursorColor = JarvisPrimaryGlow
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("input_command_field"),
                singleLine = true
            )

            // Submit Action FAB
            IconButton(
                onClick = { viewModel.sendMessage() },
                enabled = textInput.isNotBlank() && !isLoading,
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        if (textInput.isNotBlank() && !isLoading) JarvisPrimaryGlow else JarvisSurface,
                        CircleShape
                    )
                    .testTag("send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.Send,
                    contentDescription = "Transmit Command Payload",
                    tint = if (textInput.isNotBlank() && !isLoading) JarvisBackground else JarvisTextSecondary.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender == "user"

    val alignment = if (isUser) Alignment.End else Alignment.Start
    val shape = if (isUser) {
        RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp)
    }

    val bubbleBackground = if (isUser) {
        Brush.linearGradient(listOf(JarvisSurface, JarvisSurface.copy(alpha = 0.8f)))
    } else {
        Brush.linearGradient(listOf(JarvisGlowSurface.copy(alpha = 0.15f), JarvisSurface))
    }

    val bubbleBorderColor = if (isUser) {
        JarvisPrimaryGlow.copy(alpha = 0.3f)
    } else {
        JarvisSecondaryGlow.copy(alpha = 0.3f)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chat_bubble_${message.sender}"),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(bubbleBackground, shape)
                .border(BorderStroke(1.dp, bubbleBorderColor), shape)
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Sender label
                Text(
                    text = if (isUser) "STARK MASTER /" else "J.A.R.V.I.S. // CORES",
                    color = if (isUser) JarvisPrimaryGlow else JarvisSecondaryGlow,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = message.text,
                    color = JarvisTextPrimary,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun JarvisLoadingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .testTag("jarvis_loading"),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(JarvisGlowSurface, RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp))
                .border(BorderStroke(1.dp, JarvisPrimaryGlow.copy(alpha = 0.3f)), RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "J.A.R.V.I.S. // COMPILING RESPONSE...",
                    color = JarvisPrimaryGlow,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Accessing Neural cores...",
                    color = JarvisTextSecondary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                )
            }
        }
    }
}

@Composable
fun JarvisBottomNavigation(
    activeTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.height(64.dp),
        containerColor = JarvisSurface,
        tonalElevation = 6.dp
    ) {
        NavigationBarItem(
            selected = activeTab == "dashboard",
            onClick = { onTabSelected("dashboard") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Dashboard,
                    contentDescription = "Launch Diagnostics",
                    tint = if (activeTab == "dashboard") JarvisPrimaryGlow else JarvisTextSecondary
                )
            },
            label = {
                Text(
                    text = "SYNERGY",
                    color = if (activeTab == "dashboard") JarvisPrimaryGlow else JarvisTextSecondary,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = JarvisGlowSurface
            ),
            modifier = Modifier.testTag("dashboard_nav_tab")
        )

        NavigationBarItem(
            selected = activeTab == "matrix",
            onClick = { onTabSelected("matrix") },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeveloperBoard,
                    contentDescription = "Subsystems and Memory Databases",
                    tint = if (activeTab == "matrix") JarvisPrimaryGlow else JarvisTextSecondary
                )
            },
            label = {
                Text(
                    text = "SUB-MATRIX",
                    color = if (activeTab == "matrix") JarvisPrimaryGlow else JarvisTextSecondary,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = JarvisGlowSurface
            ),
            modifier = Modifier.testTag("matrix_nav_tab")
        )

        NavigationBarItem(
            selected = activeTab == "chat",
            onClick = { onTabSelected("chat") },
            icon = {
                Icon(
                    imageVector = Icons.Default.Cyclone,
                    contentDescription = "Open Cognitive Mind",
                    tint = if (activeTab == "chat") JarvisPrimaryGlow else JarvisTextSecondary
                )
            },
            label = {
                Text(
                    text = "COGNITIVE ARCH",
                    color = if (activeTab == "chat") JarvisPrimaryGlow else JarvisTextSecondary,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = JarvisGlowSurface
            ),
            modifier = Modifier.testTag("chat_nav_tab")
        )
    }
}
