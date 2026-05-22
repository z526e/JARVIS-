package com.example

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.JarvisScreen
import com.example.ui.JarvisViewModel
import com.example.ui.theme.MyApplicationTheme
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private val viewModel: JarvisViewModel by viewModels()
    private lateinit var textToSpeech: TextToSpeech
    @Volatile
    private var isTtsReady = false

    // Voice Input Speech Result Listener Activity Contract
    private val speechRecognizerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedText = results?.firstOrNull() ?: ""
            if (recognizedText.isNotBlank()) {
                viewModel.onInputTextChange(recognizedText)
                viewModel.sendMessage()
            }
        }
        viewModel.setListening(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Init Text to Speech Engine
        textToSpeech = TextToSpeech(this, this)

        setContent {
            MyApplicationTheme {
                val speakTrigger by viewModel.ttsSpeakTrigger.collectAsStateWithLifecycle()

                // Reactive observer for J.A.R.V.I.S. speech trigger events
                LaunchedEffect(speakTrigger) {
                    val textToSpeak = speakTrigger
                    if (textToSpeak != null) {
                        speakResponse(textToSpeak)
                        viewModel.clearTtsTrigger()
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    JarvisScreen(
                        viewModel = viewModel,
                        onVoiceInputClick = { startSpeechRecognition() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    override fun onInit(status: Int) {
        runOnUiThread {
            if (status == TextToSpeech.SUCCESS) {
                try {
                    // Set British British accent to sound exactly like movie JARVIS
                    val result = textToSpeech.setLanguage(Locale.UK)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        textToSpeech.setLanguage(Locale.getDefault())
                    }

                    // Sync visual Arc Reactor pulsing with utterance playback progress keys
                    textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            runOnUiThread {
                                viewModel.setSpeaking(true)
                            }
                        }

                        override fun onDone(utteranceId: String?) {
                            runOnUiThread {
                                viewModel.setSpeaking(false)
                            }
                        }

                        override fun onError(utteranceId: String?) {
                            runOnUiThread {
                                viewModel.setSpeaking(false)
                            }
                        }
                    })
                    isTtsReady = true
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "Error during JARVIS speech calibration.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@MainActivity, "Failed to initialize JARVIS Speech Synapse.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun speakResponse(text: String) {
        if (!::textToSpeech.isInitialized || !isTtsReady) return

        // Extract a clean spoken stream (stripping markdown text elements for concise playback)
        val cleanText = text.replace(Regex("[*#_`]"), "")

        val params = Bundle().apply {
            putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "jarvis_response_utterance")
        }
        try {
            textToSpeech.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, "jarvis_response_utterance")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startSpeechRecognition() {
        viewModel.setListening(true)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-BD") // Dynamic Speech recognition supports Bangla
            putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, arrayListOf("en-US", "bn-BD"))
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Sir, please state your parameter...")
        }
        try {
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            viewModel.setListening(false)
            Toast.makeText(this, "Speech recognition is active but host lacks dynamic system engine.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            try {
                textToSpeech.stop()
                textToSpeech.shutdown()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        super.onDestroy()
    }
}
