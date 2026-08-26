package com.opencode.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.opencode.chat.ChatViewModel
import com.opencode.chat.ui.theme.TermAccentBlue
import com.opencode.chat.ui.theme.TermBackground
import com.opencode.chat.ui.theme.TermBorder
import com.opencode.chat.ui.theme.TermTextDim
import com.opencode.chat.ui.theme.TermTextPrimary

@Composable
fun ConnectScreen(viewModel: ChatViewModel) {
    var serverUrl by remember { mutableStateOf("http://127.0.0.1:4096/") }
    var password by remember { mutableStateOf("") }

    val isConnecting by viewModel.isConnecting
    val error by viewModel.connectionError

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TermBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "opencode",
            fontFamily = FontFamily.Monospace,
            color = TermTextPrimary,
            fontSize = MaterialTheme.typography.titleMedium.fontSize
        )
        Spacer(Modifier.height(24.dp))
        TextField(
            value = serverUrl,
            onValueChange = { serverUrl = it },
            label = { Text("عنوان الخادم", fontFamily = FontFamily.Monospace, color = TermTextDim) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = TermBackground,
                unfocusedContainerColor = TermBackground,
                focusedIndicatorColor = TermBorder,
                unfocusedIndicatorColor = TermBorder,
                cursorColor = TermAccentBlue
            )
        )
        Spacer(Modifier.height(12.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("كلمة مرور الخادم (إن وُجدت)", fontFamily = FontFamily.Monospace, color = TermTextDim) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = TermBackground,
                unfocusedContainerColor = TermBackground,
                focusedIndicatorColor = TermBorder,
                unfocusedIndicatorColor = TermBorder,
                cursorColor = TermAccentBlue
            )
        )
        Spacer(Modifier.height(20.dp))
        Text(
            if (isConnecting) "جارٍ الاتصال..." else "› اتصال",
            fontFamily = FontFamily.Monospace,
            color = TermAccentBlue,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .clickable(enabled = !isConnecting) { viewModel.connect(serverUrl, password) }
        )
        error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error, fontFamily = FontFamily.Monospace)
        }
    }
}
