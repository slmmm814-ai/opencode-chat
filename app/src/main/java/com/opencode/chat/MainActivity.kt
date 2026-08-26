package com.opencode.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import com.opencode.chat.ui.ChatScreen
import com.opencode.chat.ui.ConnectScreen
import com.opencode.chat.ui.theme.TerminalColorScheme
import com.opencode.chat.ui.theme.TerminalTypography

class MainActivity : ComponentActivity() {
    private val viewModel: ChatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = TerminalColorScheme, typography = TerminalTypography) {
                Surface(color = TerminalColorScheme.background) {
                    val connected by viewModel.isConnected
                    if (connected) {
                        ChatScreen(viewModel)
                    } else {
                        ConnectScreen(viewModel)
                    }
                }
            }
        }
    }
}
