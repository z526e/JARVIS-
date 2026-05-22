package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.StarkItem
import com.example.ui.JarvisViewModel
import com.example.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubMatrixPanel(
    viewModel: JarvisViewModel,
    modifier: Modifier = Modifier
) {
    // Collect states
    val starkItems by viewModel.starkItems.collectAsStateWithLifecycle()
    val smartLightState by viewModel.smartLightState.collectAsStateWithLifecycle()
    val smartLightColor by viewModel.smartLightColor.collectAsStateWithLifecycle()
    val smartThermostatTemp by viewModel.smartThermostatTemp.collectAsStateWithLifecycle()
    val smartLockState by viewModel.smartLockState.collectAsStateWithLifecycle()

    val pcConnected by viewModel.pcConnected.collectAsStateWithLifecycle()
    val pcLockState by viewModel.pcLockState.collectAsStateWithLifecycle()
    val mobileSilenceMode by viewModel.mobileSilenceMode.collectAsStateWithLifecycle()

    val productCost by viewModel.productCost.collectAsStateWithLifecycle()
    val productShipping by viewModel.productShipping.collectAsStateWithLifecycle()
    val productSalePrice by viewModel.productSalePrice.collectAsStateWithLifecycle()
    val productSalesVolume by viewModel.productSalesVolume.collectAsStateWithLifecycle()

    var activeSubTab by remember { mutableStateOf("database") } // "database", "home", "amazon"

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // High-tech Segment Toggle Taps
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(JarvisSurface, RoundedCornerShape(8.dp))
                .border(BorderStroke(1.dp, JarvisBorder), RoundedCornerShape(8.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val tabs = listOf(
                "database" to "STARK CORE", 
                "home" to "SMART HOME", 
                "amazon" to "AMAZON RESEARCH"
            )
            tabs.forEach { (key, label) ->
                val isSelected = activeSubTab == key
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) JarvisGlowSurface else Color.Transparent)
                        .border(
                            BorderStroke(
                                1.dp,
                                if (isSelected) JarvisPrimaryGlow.copy(alpha = 0.6f) else Color.Transparent
                            ),
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { activeSubTab = key }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) JarvisPrimaryGlow else JarvisTextSecondary,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Animated content panels
        when (activeSubTab) {
            "database" -> PersonalCorePane(
                starkItems = starkItems,
                onAddItem = { type, title, desc, dt -> viewModel.addStarkItem(type, title, desc, dt) },
                onToggleReminder = { viewModel.toggleStarkItemCompleted(it) },
                onDeleteItem = { viewModel.deleteStarkItemById(it) }
            )
            "home" -> SmartHomePane(
                lightState = smartLightState,
                lightColor = smartLightColor,
                temp = smartThermostatTemp,
                lockState = smartLockState,
                pcConnected = pcConnected,
                pcLock = pcLockState,
                mobileSilence = mobileSilenceMode,
                onLightChange = { viewModel.updateSmartLight(it) },
                onLightColorChange = { viewModel.updateSmartLightColor(it) },
                onTempChange = { viewModel.updateSmartThermostat(it) },
                onLockChange = { viewModel.updateSmartLock(it) },
                onPcConnectChange = { viewModel.updatePcConnected(it) },
                onPcLockChange = { viewModel.updatePcLock(it) },
                onMobileSilenceChange = { viewModel.updateMobileSilence(it) }
            )
            "amazon" -> AmazonResearchPane(
                cost = productCost,
                shipping = productShipping,
                salePrice = productSalePrice,
                sales = productSalesVolume,
                onValuesChange = { c, sh, s, sv -> viewModel.updateProductResearch(c, sh, s, sv) }
            )
        }
    }
}

@Composable
fun PersonalCorePane(
    starkItems: List<StarkItem>,
    onAddItem: (String, String, String, String) -> Unit,
    onToggleReminder: (StarkItem) -> Unit,
    onDeleteItem: (Long) -> Unit
) {
    // Form States
    var itemType by remember { mutableStateOf("note") } // "note", "reminder", "event"
    var itemTitle by remember { mutableStateOf("") }
    var itemContent by remember { mutableStateOf("") }
    var itemDatetime by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = JarvisSurface.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, JarvisBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "SYNAPSE RECORD MATRIX //",
                        color = JarvisPrimaryGlow,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    // Type Selector Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("note" to "Note", "reminder" to "Reminder", "event" to "Event").forEach { (key, label) ->
                            val active = itemType == key
                            OutlinedButton(
                                onClick = { itemType = key },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (active) JarvisGlowSurface else Color.Transparent,
                                    contentColor = if (active) JarvisPrimaryGlow else JarvisTextSecondary
                                ),
                                border = BorderStroke(1.dp, if (active) JarvisPrimaryGlow else JarvisBorder),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(label, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }

                    // Fields
                    OutlinedTextField(
                        value = itemTitle,
                        onValueChange = { itemTitle = it },
                        label = { Text("Title / Action Item", fontSize = 11.sp, color = JarvisTextSecondary) },
                        textStyle = LocalTextStyle.current.copy(color = JarvisTextPrimary, fontSize = 13.sp, fontFamily = FontFamily.Monospace),
                        shape = RoundedCornerShape(6.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JarvisPrimaryGlow,
                            unfocusedBorderColor = JarvisBorder,
                            focusedLabelColor = JarvisPrimaryGlow,
                            unfocusedLabelColor = JarvisTextSecondary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (itemType == "note") {
                        OutlinedTextField(
                            value = itemContent,
                            onValueChange = { itemContent = it },
                            label = { Text("Database Notes Briefing", fontSize = 11.sp, color = JarvisTextSecondary) },
                            textStyle = LocalTextStyle.current.copy(color = JarvisTextPrimary, fontSize = 13.sp, fontFamily = FontFamily.Monospace),
                            shape = RoundedCornerShape(6.dp),
                            maxLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = JarvisPrimaryGlow,
                                unfocusedBorderColor = JarvisBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        OutlinedTextField(
                            value = itemDatetime,
                            onValueChange = { itemDatetime = it },
                            placeholder = { Text("e.g. Tomorrow 10:00 AM / 18:00", fontSize = 10.sp, color = JarvisTextSecondary) },
                            label = { Text("Time Protocol", fontSize = 11.sp, color = JarvisTextSecondary) },
                            textStyle = LocalTextStyle.current.copy(color = JarvisTextPrimary, fontSize = 13.sp, fontFamily = FontFamily.Monospace),
                            shape = RoundedCornerShape(6.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = JarvisPrimaryGlow,
                                unfocusedBorderColor = JarvisBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Button(
                        onClick = {
                            if (itemTitle.isNotBlank()) {
                                val finalDt = if (itemType == "note") "" else itemDatetime
                                onAddItem(itemType, itemTitle, itemContent, finalDt)
                                itemTitle = ""
                                itemContent = ""
                                itemDatetime = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = JarvisPrimaryGlow),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("COMMIT TO STARK DATABASE", color = JarvisBackground, fontWeight = FontWeight.Bold, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        item {
            Text(
                text = "ACTIVE RECORDS (${starkItems.size}) //",
                color = JarvisSecondaryGlow,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (starkItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sir, database logs are fully synchronized. No active pending schedules or records detected.",
                        color = JarvisTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.alpha(0.6f)
                    )
                }
            }
        }

        items(starkItems, key = { it.id }) { item ->
            val tintColor = when (item.type) {
                "note" -> JarvisPrimaryGlow
                "reminder" -> JarvisSecondaryGlow
                else -> JarvisSuccess
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = JarvisSurface),
                border = BorderStroke(1.dp, JarvisBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Type Badge Icon
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(tintColor.copy(alpha = 0.15f), CircleShape)
                            .border(BorderStroke(1.dp, tintColor.copy(alpha = 0.5f)), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (item.type) {
                                "note" -> Icons.Default.StickyNote2
                                "reminder" -> Icons.Default.NotificationsActive
                                else -> Icons.Default.CalendarMonth
                            },
                            contentDescription = "Item Indicator",
                            tint = tintColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Text Details
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.type.uppercase() + " //",
                                color = tintColor,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            if (item.datetimeString.isNotBlank()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.datetimeString,
                                    color = JarvisTextSecondary,
                                    fontSize = 8.5.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Text(
                            text = item.title,
                            color = JarvisTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        if (item.content.isNotBlank()) {
                            Text(
                                text = item.content,
                                color = JarvisTextSecondary,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Actions
                    if (item.type == "reminder") {
                        IconButton(onClick = { onToggleReminder(item) }) {
                            Icon(
                                imageVector = if (item.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Toggle Complete",
                                tint = if (item.isCompleted) JarvisSuccess else JarvisTextSecondary
                            )
                        }
                    }

                    IconButton(onClick = { onDeleteItem(item.id) }) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = "Purge Archive Item",
                            tint = Color.Red.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SmartHomePane(
    lightState: Boolean,
    lightColor: String,
    temp: Int,
    lockState: Boolean,
    pcConnected: Boolean,
    pcLock: Boolean,
    mobileSilence: Boolean,
    onLightChange: (Boolean) -> Unit,
    onLightColorChange: (String) -> Unit,
    onTempChange: (Int) -> Unit,
    onLockChange: (Boolean) -> Unit,
    onPcConnectChange: (Boolean) -> Unit,
    onPcLockChange: (Boolean) -> Unit,
    onMobileSilenceChange: (Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Smart Home Node
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = JarvisSurface),
                border = BorderStroke(1.dp, JarvisBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "HOLOGRAPHIC HOME DIRECTIVE //",
                        color = JarvisPrimaryGlow,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    // Light Toggle Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Living Room Glow arrays", color = JarvisTextPrimary, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                            Text(
                                text = if (lightState) "ONLINE // ACTIVE COLOR: $lightColor" else "OFFLINE",
                                color = if (lightState) JarvisSuccess else JarvisTextSecondary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Switch(
                            checked = lightState,
                            onCheckedChange = { onLightChange(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = JarvisPrimaryGlow,
                                checkedTrackColor = JarvisGlowSurface
                            )
                        )
                    }

                    // Colors selection list
                    if (lightState) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Spectra:", color = JarvisTextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            val colors = listOf("Teal Cyan", "Cyber Amber", "Fusion Gold", "Proton Violet")
                            colors.forEach { name ->
                                val active = lightColor == name
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(if (active) JarvisGlowSurface else Color.Transparent, RoundedCornerShape(4.dp))
                                        .border(BorderStroke(1.dp, if (active) JarvisPrimaryGlow else JarvisBorder), RoundedCornerShape(4.dp))
                                        .clickable { onLightColorChange(name) }
                                        .padding(vertical = 4.dp, horizontal = 2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(name.substringBefore(" "), color = if (active) JarvisPrimaryGlow else JarvisTextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }

                    Divider(color = JarvisBorder)

                    // Thermostat Adjustment
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Thermostat Thermal Calibration", color = JarvisTextPrimary, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                            Text(
                                text = "TARGET TEMP: ${temp}°C",
                                color = JarvisPrimaryGlow,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Adjustment controller buttons
                        Row(
                            modifier = Modifier
                                .border(BorderStroke(1.dp, JarvisBorder), RoundedCornerShape(6.dp))
                                .background(JarvisBackground),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { if (temp > 15) onTempChange(temp - 1) }) {
                                Icon(Icons.Default.Remove, "Cool", tint = JarvisPrimaryGlow, modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = "$temp°C",
                                color = JarvisTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            IconButton(onClick = { if (temp < 32) onTempChange(temp + 1) }) {
                                Icon(Icons.Default.Add, "Heat", tint = JarvisPrimaryGlow, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Divider(color = JarvisBorder)

                    // Armed lock System
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Security Perimeter Portal Gate", color = JarvisTextPrimary, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                            Text(
                                text = if (lockState) "ARMED / ENCRYPTED / SECURED" else "DISARMED / ACCESS GRANTED",
                                color = if (lockState) JarvisPrimaryGlow else JarvisSecondaryGlow,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(
                            onClick = { onLockChange(!lockState) },
                            modifier = Modifier
                                .background(if (lockState) JarvisGlowSurface else Color.Red.copy(alpha = 0.1f), CircleShape)
                                .border(BorderStroke(1.dp, if (lockState) JarvisPrimaryGlow else Color.Red), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (lockState) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = "Toggle Portal Secure",
                                tint = if (lockState) JarvisPrimaryGlow else Color.Red
                            )
                        }
                    }
                }
            }
        }

        // Hardware Sync Controls
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = JarvisSurface),
                border = BorderStroke(1.dp, JarvisBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "SYNAPSE HARDWARE NODE LINKAGE //",
                        color = JarvisSecondaryGlow,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    // PC Connect
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Stark Master Personal Desktop PC", color = JarvisTextPrimary, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                            Text(
                                text = if (pcConnected) "CONNECTED // SYSTEM LOCK ACTIVE: $pcLock" else "DISCONNECTED",
                                color = if (pcConnected) JarvisSuccess else JarvisTextSecondary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Switch(
                            checked = pcConnected,
                            onCheckedChange = { onPcConnectChange(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = JarvisSecondaryGlow,
                                checkedTrackColor = JarvisSecondaryGlow.copy(alpha = 0.2f)
                            )
                        )
                    }

                    if (pcConnected) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(JarvisBackground, RoundedCornerShape(6.dp))
                                .border(BorderStroke(1.dp, JarvisBorder), RoundedCornerShape(6.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("PC Lock Override", color = JarvisTextSecondary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            Button(
                                onClick = { onPcLockChange(!pcLock) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (pcLock) JarvisSecondaryGlow else JarvisSurface
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, JarvisSecondaryGlow)
                            ) {
                                Text(
                                    text = if (pcLock) "UNLOCK RIG" else "LOCK RIG",
                                    color = if (pcLock) JarvisBackground else JarvisSecondaryGlow,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Divider(color = JarvisBorder)

                    // Mobile Silence Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Mobile Interrupt Suppress Matrix", color = JarvisTextPrimary, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                            Text(
                                text = if (mobileSilence) "SILENT MODE // SUPPRESSED" else "NOMINAL AUDIBLES ACTIVE",
                                color = if (mobileSilence) JarvisPrimaryGlow else JarvisTextSecondary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Switch(
                            checked = mobileSilence,
                            onCheckedChange = { onMobileSilenceChange(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = JarvisPrimaryGlow,
                                checkedTrackColor = JarvisGlowSurface
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AmazonResearchPane(
    cost: Double,
    shipping: Double,
    salePrice: Double,
    sales: Int,
    onValuesChange: (Double, Double, Double, Int) -> Unit
) {
    // Local editable text states (to avoid key lag while typing numbers)
    var costTxt by remember(cost) { mutableStateOf(cost.toString()) }
    var shipTxt by remember(shipping) { mutableStateOf(shipping.toString()) }
    var saleTxt by remember(salePrice) { mutableStateOf(salePrice.toString()) }
    var salesTxt by remember(sales) { mutableStateOf(sales.toString()) }

    // Live Math Computations
    val parsedCost = costTxt.toDoubleOrNull() ?: 0.0
    val parsedShip = shipTxt.toDoubleOrNull() ?: 0.0
    val parsedSale = saleTxt.toDoubleOrNull() ?: 0.0
    val parsedSales = salesTxt.toIntOrNull() ?: 0

    // Typical Fulfillment fees (15% referral + typical $4.50 FBA dispatch pick pack)
    val referralFee = parsedSale * 0.15
    val fbaFee = 4.50
    val totalFees = referralFee + fbaFee

    val totalCost = parsedCost + parsedShip
    val netProfitUnit = if (parsedSale > 0) parsedSale - totalCost - totalFees else 0.0
    val netProfitMarginPct = if (parsedSale > 0) (netProfitUnit / parsedSale) * 100.0 else 0.0
    val roiPct = if (totalCost > 0) (netProfitUnit / totalCost) * 100.0 else 0.0

    val monthlyRevenue = parsedSale * parsedSales
    val monthlyProfit = netProfitUnit * parsedSales

    // Opportunity grading system
    val score = when {
        netProfitMarginPct >= 35.0 && roiPct >= 100.0 -> "S-RANK OPPORTUNITY"
        netProfitMarginPct >= 25.0 && roiPct >= 80.0 -> "HIGHLY POWERFUL NOMINAL"
        netProfitMarginPct >= 15.0 && roiPct >= 50.0 -> "MODERATE MARGIN"
        else -> "LOW VIABILITY PRODUCT WARNING"
    }

    val scoreColor = when {
        netProfitMarginPct >= 35.0 && roiPct >= 100.0 -> JarvisSuccess
        netProfitMarginPct >= 20.0 && roiPct >= 60.0 -> JarvisPrimaryGlow
        netProfitMarginPct >= 12.0 && roiPct >= 30.0 -> JarvisSecondaryGlow
        else -> Color.Red
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = JarvisSurface),
                border = BorderStroke(1.dp, JarvisBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "STARK AMAZON PRODUCT ARCHITECT //",
                        color = JarvisPrimaryGlow,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Sir, feed targeted arbitrage and sourcing indicators below to run instant neural margin and scalability evaluations.",
                        color = JarvisTextSecondary,
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 14.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = costTxt,
                            onValueChange = { 
                                costTxt = it
                                onValuesChange(it.toDoubleOrNull() ?: 0.0, parsedShip, parsedSale, parsedSales)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text("Product Cost ($)", fontSize = 10.sp, color = JarvisTextSecondary) },
                            textStyle = TextStyle(color = JarvisTextPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = shipTxt,
                            onValueChange = { 
                                shipTxt = it
                                onValuesChange(parsedCost, it.toDoubleOrNull() ?: 0.0, parsedSale, parsedSales)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text("Shipping ($)", fontSize = 10.sp, color = JarvisTextSecondary) },
                            textStyle = TextStyle(color = JarvisTextPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = saleTxt,
                            onValueChange = { 
                                saleTxt = it
                                onValuesChange(parsedCost, parsedShip, it.toDoubleOrNull() ?: 0.0, parsedSales)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text("Sale Price ($)", fontSize = 10.sp, color = JarvisTextSecondary) },
                            textStyle = TextStyle(color = JarvisTextPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = salesTxt,
                            onValueChange = { 
                                salesTxt = it
                                onValuesChange(parsedCost, parsedShip, parsedSale, it.toIntOrNull() ?: 0)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text("Mo. Sales Volume", fontSize = 10.sp, color = JarvisTextSecondary) },
                            textStyle = TextStyle(color = JarvisTextPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Analytical Results Core
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = JarvisSurface),
                border = BorderStroke(1.dp, JarvisBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "ANALYTICAL METRICS ANALYSIS //",
                        color = JarvisSecondaryGlow,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    // Scoring
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(scoreColor.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                            .border(BorderStroke(1.dp, scoreColor.copy(alpha = 0.5f)), RoundedCornerShape(6.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("VIABILITY SUCCESS CLASSIFICATION", color = JarvisTextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Text(
                                text = score,
                                color = scoreColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // Profit math grids
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(JarvisBackground, RoundedCornerShape(6.dp))
                                .border(BorderStroke(1.dp, JarvisBorder), RoundedCornerShape(6.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text("FBA FEES (UNIT)", color = JarvisTextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Text(text = "$${String.format(Locale.UK, "%.2f", totalFees)}", color = JarvisSecondaryGlow, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(JarvisBackground, RoundedCornerShape(6.dp))
                                .border(BorderStroke(1.dp, JarvisBorder), RoundedCornerShape(6.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text("NET PROFIT (UNIT)", color = JarvisTextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Text(text = "$${String.format(Locale.UK, "%.2f", netProfitUnit)}", color = JarvisPrimaryGlow, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(JarvisBackground, RoundedCornerShape(6.dp))
                                .border(BorderStroke(1.dp, JarvisBorder), RoundedCornerShape(6.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text("NET PROFIT MARGIN", color = JarvisTextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Text(text = "${String.format(Locale.UK, "%.1f", netProfitMarginPct)}%", color = scoreColor, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(JarvisBackground, RoundedCornerShape(6.dp))
                                .border(BorderStroke(1.dp, JarvisBorder), RoundedCornerShape(6.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text("ESTIMATED ROI", color = JarvisTextSecondary, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Text(text = "${String.format(Locale.UK, "%.1f", roiPct)}%", color = scoreColor, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Divider(color = JarvisBorder)

                    // Scaling revenues
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Calculated Monthly Revenue Output", color = JarvisTextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            Text(text = "$${String.format(Locale.UK, "%.2f", (monthlyRevenue).toDouble())}", color = JarvisTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Calculated Monthly Profit Output", color = JarvisTextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            Text(text = "$${String.format(Locale.UK, "%.2f", (monthlyProfit).toDouble())}", color = JarvisSuccess, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}
