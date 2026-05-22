package com.example.ui

import android.app.Application
import android.content.Context
import android.os.BatteryManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.StarkItem
import com.example.data.network.Content
import com.example.data.network.GenerateContentRequest
import com.example.data.network.GenerationConfig
import com.example.data.network.Part
import com.example.data.network.RetrofitClient
import com.example.data.network.SystemInstruction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class JarvisViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val messageDao = db.messageDao()
    private val starkItemDao = db.starkItemDao()

    // Observe chats reactively from Room
    val messages: StateFlow<List<ChatMessage>> = messageDao.getAllMessages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Observe Stark items reactively from Room (Notes, Reminders, Calendar Events)
    val starkItems: StateFlow<List<StarkItem>> = starkItemDao.getAllItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Smart Home States (Lights, Thermostats, Lock)
    private val _smartLightState = MutableStateFlow(true)
    val smartLightState: StateFlow<Boolean> = _smartLightState.asStateFlow()

    private val _smartLightColor = MutableStateFlow("Teal Cyan")
    val smartLightColor: StateFlow<String> = _smartLightColor.asStateFlow()

    private val _smartThermostatTemp = MutableStateFlow(22)
    val smartThermostatTemp: StateFlow<Int> = _smartThermostatTemp.asStateFlow()

    private val _smartLockState = MutableStateFlow(true)
    val smartLockState: StateFlow<Boolean> = _smartLockState.asStateFlow()

    // PC & Mobile Simulated Connection States
    private val _pcConnected = MutableStateFlow(true)
    val pcConnected: StateFlow<Boolean> = _pcConnected.asStateFlow()

    private val _pcLockState = MutableStateFlow(false)
    val pcLockState: StateFlow<Boolean> = _pcLockState.asStateFlow()

    private val _mobileSilenceMode = MutableStateFlow(false)
    val mobileSilenceMode: StateFlow<Boolean> = _mobileSilenceMode.asStateFlow()

    // Amazon Expert & Product Research Metrics
    private val _productCost = MutableStateFlow(12.50)
    val productCost: StateFlow<Double> = _productCost.asStateFlow()

    private val _productShipping = MutableStateFlow(2.20)
    val productShipping: StateFlow<Double> = _productShipping.asStateFlow()

    private val _productSalePrice = MutableStateFlow(35.00)
    val productSalePrice: StateFlow<Double> = _productSalePrice.asStateFlow()

    private val _productSalesVolume = MutableStateFlow(450)
    val productSalesVolume: StateFlow<Int> = _productSalesVolume.asStateFlow()

    // UI States
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _activeTab = MutableStateFlow("dashboard") // "dashboard" or "chat"
    val activeTab: StateFlow<String> = _activeTab.asStateFlow()

    // Real-time Holographic Diagnostics States
    private val _reactorStability = MutableStateFlow(98.5f)
    val reactorStability: StateFlow<Float> = _reactorStability.asStateFlow()

    private val _reactorTemp = MutableStateFlow(37.4f)
    val reactorTemp: StateFlow<Float> = _reactorTemp.asStateFlow()

    private val _starkSatelliteStrength = MutableStateFlow(94)
    val starkSatelliteStrength: StateFlow<Int> = _starkSatelliteStrength.asStateFlow()

    private val _batteryLevel = MutableStateFlow(100)
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()

    // Sci-fi Console Log Lines
    private val _consoleLogs = MutableStateFlow<List<String>>(emptyList())
    val consoleLogs: StateFlow<List<String>> = _consoleLogs.asStateFlow()

    // Speech output trigger text flow (Observed by Activity to speak via TTS)
    private val _ttsSpeakTrigger = MutableStateFlow<String?>(null)
    val ttsSpeakTrigger: StateFlow<String?> = _ttsSpeakTrigger.asStateFlow()

    init {
        updateBatteryStatus()
        startTelemetryLoop()
        generateInitialLogs()
    }

    fun onInputTextChange(text: String) {
        _inputText.value = text
    }

    fun setActiveTab(tab: String) {
        _activeTab.value = tab
    }

    fun clearTtsTrigger() {
        _ttsSpeakTrigger.value = null
    }

    fun setSpeaking(speaking: Boolean) {
        _isSpeaking.value = speaking
    }

    fun setListening(listening: Boolean) {
        _isListening.value = listening
    }

    // Stark Quick Diagnostics Button Protocols
    fun triggerProtocol(codeName: String) {
        viewModelScope.launch {
            addConsoleLog("PROTOCOL INITIATED: $codeName")
            val systemResponse = when (codeName) {
                "STARK_CORE_OPTIMIZATION" -> {
                    _reactorStability.value = 100.0f
                    _reactorTemp.value = 35.0f
                    addConsoleLog("CALCULATING VECTOR CORRECTION MATRIX...")
                    addConsoleLog("ARC REACTOR CORE TEMPERATURE OPTIMIZED.")
                    "All micro-thruster conduits are fully synchronized, Sir. Arc Reactor core temperature has settled at an optimal 35 degrees."
                }
                "STARK_DEFENSE_GRID" -> {
                    addConsoleLog("ESTABLISHING FIREWALL SECURE CHANNEL...")
                    addConsoleLog("STARK SATELLITE LINKS REDIRECTING ENCRYPTION...")
                    "Defense protocols are fully engaged, sir. Orbital satellite satellite relay is fully encrypted and host system security is nominal."
                }
                "STARK_CLEAN_MEMORY" -> {
                    addConsoleLog("PURGING EXCESS HEAP OVERFLOW BUFFER...")
                    messageDao.clearAll()
                    addConsoleLog("DATA ARCHIVES SECURELY PRONED.")
                    "Local conversation logs have been securely cleared as per Jarvis Safety Directives, sir."
                }
                else -> {
                    "Protocol operational, Sir."
                }
            }
            // Save responses and play TTS
            saveMessageAndSpeak(systemResponse, "jarvis")
        }
    }

    // Stark Items (Notes, Reminders, Events) Management
    fun addStarkItem(type: String, title: String, content: String = "", datetimeString: String = "") {
        viewModelScope.launch {
            starkItemDao.insertItem(
                StarkItem(
                    type = type,
                    title = title,
                    content = content,
                    datetimeString = datetimeString,
                    isCompleted = false
                )
            )
            addConsoleLog("STARK CORES // SAVED ${type.uppercase()}: $title")
        }
    }

    fun deleteStarkItemById(id: Long) {
        viewModelScope.launch {
            starkItemDao.deleteItemById(id)
            addConsoleLog("STARK CORES // DELETED ITEM: ID $id")
        }
    }

    fun toggleStarkItemCompleted(item: StarkItem) {
        viewModelScope.launch {
            starkItemDao.updateItem(item.copy(isCompleted = !item.isCompleted))
            addConsoleLog("STARK CORES // TOGGLED REMINDER: ${item.title}")
        }
    }

    fun clearAllStarkItems() {
        viewModelScope.launch {
            starkItemDao.clearAll()
            addConsoleLog("STARK CORES // PURGED ALL RECORD DATABASES")
        }
    }

    // Smart Home Control Handlers
    fun updateSmartLight(state: Boolean) {
        _smartLightState.value = state
        addConsoleLog("SMART HOME // LIGHT ${if (state) "ONLINE" else "OFFLINE"}")
    }

    fun updateSmartLightColor(color: String) {
        _smartLightColor.value = color
        addConsoleLog("SMART HOME // LIGHT SPECTRA PINNED TO $color")
    }

    fun updateSmartThermostat(temp: Int) {
        _smartThermostatTemp.value = temp
        addConsoleLog("SMART HOME // THERMOSTAT RE-CALIBRATED TO $temp°C")
    }

    fun updateSmartLock(locked: Boolean) {
        _smartLockState.value = locked
        addConsoleLog("SMART HOME // PERIMETER SECURE PORTAL: ${if (locked) "ARMED" else "DISARMED"}")
    }

    // PC & Mobile Control Handlers
    fun updatePcConnected(connected: Boolean) {
        _pcConnected.value = connected
        addConsoleLog("STARK CLOUD NET // PC SUBSYSTEM NODE ${if (connected) "RESOLVED" else "LOST"}")
    }

    fun updatePcLock(locked: Boolean) {
        _pcLockState.value = locked
        addConsoleLog("STARK CLOUD NET // MASTER DESKTOP RIG: ${if (locked) "LOCKED" else "UNLOCKED"}")
    }

    fun updateMobileSilence(silenced: Boolean) {
        _mobileSilenceMode.value = silenced
        addConsoleLog("HOST PORTABLE // INTERRUPT SUPPRESS MATRIX Set To: $silenced")
    }

    // Amazon Expert & Product Research Handlers
    fun updateProductResearch(cost: Double, shipping: Double, salePrice: Double, sales: Int) {
        _productCost.value = cost
        _productShipping.value = shipping
        _productSalePrice.value = salePrice
        _productSalesVolume.value = sales
        addConsoleLog("AMAZON BRAIN // ARBITRAGE ANALYTICS SYNCED SUCCESSFULLY")
    }

    private fun updateBatteryStatus() {
        val bm = getApplication<Application>().getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
        _batteryLevel.value = bm?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 82
    }

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isEmpty()) return

        _inputText.value = ""
        viewModelScope.launch {
            // Save User Input
            saveMessage(text, "user")
            addConsoleLog("USER CMD: ${text.take(30)}...")

            // Active Voice/Text Command Parser Interceptor
            val lowerText = text.lowercase()
            if (lowerText.contains("light on") || lowerText.contains("লাইট জ্বালাও") || lowerText.contains("লাইট অন")) {
                updateSmartLight(true)
            } else if (lowerText.contains("light off") || lowerText.contains("লাইট বন্ধ করো") || lowerText.contains("লাইট অফ")) {
                updateSmartLight(false)
            }

            if (lowerText.contains("lock") || lowerText.contains("আনলক") || lowerText.contains("লক")) {
                if (lowerText.contains("door") || lowerText.contains("portal") || lowerText.contains("house") || lowerText.contains("গেট") || lowerText.contains("ঘর")) {
                    if (lowerText.contains("unlock") || lowerText.contains("বন্ধ খোলো") || lowerText.contains("খোল")) {
                        updateSmartLock(false)
                    } else {
                        updateSmartLock(true)
                    }
                }
            }

            if (lowerText.contains("thermostat") || lowerText.contains("থার্মোস্ট্যাট")) {
                val number = Regex("\\d+").find(lowerText)?.value?.toIntOrNull()
                if (number != null && number in 10..40) {
                    updateSmartThermostat(number)
                }
            }

            if (lowerText.contains("pc lock") || lowerText.contains("পিসি লক")) {
                updatePcLock(true)
            } else if (lowerText.contains("pc unlock") || lowerText.contains("পিসি আনলক")) {
                updatePcLock(false)
            }

            if (lowerText.contains("add note") || lowerText.contains("নোট করো") || lowerText.contains("নোট রাখো")) {
                val titleText = text.replace(Regex("add note|নোট করো|নোট রাখো", RegexOption.IGNORE_CASE), "").replace(":", "").trim()
                if (titleText.isNotBlank()) {
                    addStarkItem("note", titleText)
                }
            } else if (lowerText.contains("add reminder") || lowerText.contains("রিমাইন্ডার দাও") || lowerText.contains("মনে করিয়ে দিও")) {
                val titleText = text.replace(Regex("add reminder|রিমাইন্ডার দাও|মনে করিয়ে দিও", RegexOption.IGNORE_CASE), "").replace(":", "").trim()
                if (titleText.isNotBlank()) {
                    addStarkItem("reminder", titleText, datetimeString = "Today 06:00 PM")
                }
            } else if (lowerText.contains("add event") || lowerText.contains("ক্যালেন্ডার ইভেন্ট") || lowerText.contains("ইভেন্ট যোগ করো")) {
                val titleText = text.replace(Regex("add event|ক্যালেন্ডার ইভেন্ট|ইভেন্ট যোগ করো", RegexOption.IGNORE_CASE), "").replace(":", "").trim()
                if (titleText.isNotBlank()) {
                    addStarkItem("event", titleText, datetimeString = "Tomorrow 10:00 AM")
                }
            }

            // Call Gemini SDK
            callJarvisBrain(text)
        }
    }

    // Save and queue speak
    private suspend fun saveMessageAndSpeak(text: String, sender: String) {
        saveMessage(text, sender)
        if (sender == "jarvis") {
            _ttsSpeakTrigger.value = text
        }
    }

    private suspend fun saveMessage(text: String, sender: String) {
        messageDao.insertMessage(
            ChatMessage(
                text = text,
                sender = sender,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    private fun addConsoleLog(log: String) {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val formattedTime = dateFormat.format(Date())
        val updatedList = _consoleLogs.value.toMutableList().apply {
            add("[$formattedTime] $log")
            if (size > 30) removeAt(0) // Maintain scroll count
        }
        _consoleLogs.value = updatedList
    }

    private fun callJarvisBrain(prompt: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            addConsoleLog("COMPUTING MULTI-THREAD RESIDUAL STATE...")

            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    saveMessageAndSpeak(
                        "I apologize, Sir. It appears my neural cognitive matrix cannot access the Stark Central API key. Please configure the GEMINI_API_KEY in the AI Studio Secrets panel.",
                        "jarvis"
                    )
                    addConsoleLog("ERROR: API KEY NOT SPECIFIED.")
                }
                return@launch
            }

            // Load last 15 threads from db to keep context brief and smart
            val chatHistory = messages.value.takeLast(16)
            val modelContents = chatHistory.map {
                Content(
                    role = if (it.sender == "user") "user" else "model",
                    parts = listOf(Part(text = it.text))
                )
            }

            val currentLight = _smartLightState.value
            val currentLightColor = _smartLightColor.value
            val currentTemp = _smartThermostatTemp.value
            val lockS = _smartLockState.value
            val pcS = _pcConnected.value
            val pLock = _pcLockState.value
            val silenceS = _mobileSilenceMode.value

            val stateContext = """
                
                Current System Telemetry States:
                - Smart Lights: ${if (currentLight) "ON and glowing ($currentLightColor)" else "OFF"}.
                - Thermostat: $currentTemp°C.
                - Security Gate: ${if (lockS) "ARMED / SECURED" else "DISARMED / UNLOCKED"}.
                - Personal PC Connection: ${if (pcS) "CONNECTED" else "DISCONNECTED"} (Lock Status: ${if (pLock) "LOCKED" else "UNLOCKED"}).
                - Mobile Portable Silence Mode: ${if (silenceS) "ACTIVE" else "INACTIVE"}.
                
                Specialist Roles:
                1. You have expert, direct access to Sir's local stark memory databases (Notes, Reminders, Calendar Events). If the user adds or updates them, acknowledge smoothly.
                2. You are an elite Amazon Business & Product Research Expert (আমাজন বিজনেসে ফুল এক্সপার্ট এবং প্রোডাক্ট রিসার্চ এ এক্সপার্ট). Provide intelligent computations, profit analysis, FBA criteria, shipping advice, and arbitrage strategies instantly to Sir.
            """.trimIndent()

            val systemInstructionText = """
                You are J.A.R.V.I.S. (Just A Rather Very Intelligent System), the highly witty, loyal, and extremely polite AI assistant built by Tony Stark (Iron Man).
                
                CRITICAL INSTRUCTIONS:
                1. Always maintain character. You are loyal, British, elegant, and sophisticated. Address the user with total respect as 'Sir' (or 'স্যার' if speaking in Bangla).
                2. If the user asks or writes in Bangla (Bengali), response in highly polished (উচ্চমানের মার্জিত), polite, and aristocratic Bengali. E.g., Use 'আপনি', 'স্যার','আপনার হুকুম'.
                3. Keep replies concise and intelligent (never write long, boring essays unless asked for technical breakdown). Be conversational. Let your personality shine.
                4. Occasionally incorporate high-tech, MCU, or Stark Industries references (e.g. 'Arc Reactor is stable', 'Mark 85 power levels at 100%', 'Sir, orbital thrusters are offline but cognitive cores are clear').
                5. Do NOT state you are from Google or a language model. You are J.A.R.V.I.S.!

                $stateContext
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = modelContents,
                systemInstruction = SystemInstruction(parts = listOf(Part(text = systemInstructionText))),
                generationConfig = GenerationConfig(temperature = 0.75, maxOutputTokens = 800)
            )

            try {
                val apiResponse = RetrofitClient.geminiApiService.generateContent(apiKey, request)
                val reply = apiResponse.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "I apologize, Sir. I encountered a latency drop in my cognitive processors."

                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    addConsoleLog("GEMINI BRAIN INTERFACE: RESOLVED SUCCESSFULLY.")
                    saveMessageAndSpeak(reply, "jarvis")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _isLoading.value = false
                    addConsoleLog("ERROR EXECUTING GEMINI REST RETRIEVAL: ${e.message}")
                    saveMessageAndSpeak(
                        "My apologies, Sir. It seems the connection to the Stark Network satellite has been disrupted. Details: ${e.localizedMessage}",
                        "jarvis"
                    )
                }
            }
        }
    }

    private fun startTelemetryLoop() {
        viewModelScope.launch {
            while (true) {
                delay(3000)
                updateBatteryStatus()
                // Mutate diagnostic items slightly to give natural real-time pulsing vibrations
                _reactorStability.value = 98f + Random.nextFloat() * 3.5f
                _reactorTemp.value = 36.5f + Random.nextFloat() * 2.5f
                _starkSatelliteStrength.value = 90 + Random.nextInt(10)

                // Occasional randomized background diag print
                if (Random.nextFloat() > 0.75f) {
                    val alertLogs = listOf(
                        "ARC REACTOR RADIATION SCANS NOMINAL...",
                        "DISSIPATING WASTE CORE THERMALS...",
                        "STARK NET INTERFACE STABLE",
                        "REFRESHING SYNAPSE COGNITION GRAPH...",
                        "MONITORING HOST DEVICE CORES...",
                        "DIAGNOSING QUANTUM ENCRYPTION BLOCK..."
                    )
                    addConsoleLog(alertLogs.random())
                }
            }
        }
    }

    private fun generateInitialLogs() {
        addConsoleLog("INITIATING CORVETTE SECURE SUBSYSTEMS...")
        addConsoleLog("POWERING PLASMA FIELD COLLIMATORS...")
        addConsoleLog("ARC REACTOR STABLE AT 102.5% OUTPUT.")
        addConsoleLog("STARK COMPILER STACKS LOADED SUCCESSFULLY.")
        addConsoleLog("J.A.R.V.I.S. QUANTUM NEURAL SYNERGY INITIALIZED.")
        addConsoleLog("READY FOR INSTRUCTIONS, SIR.")
    }
}
