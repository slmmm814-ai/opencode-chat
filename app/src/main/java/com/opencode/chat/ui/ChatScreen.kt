package com.opencode.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.opencode.chat.ChatViewModel
import com.opencode.chat.data.ChatMessage
import com.opencode.chat.ui.theme.TermAccentBlue
import com.opencode.chat.ui.theme.TermAccentOrange
import com.opencode.chat.ui.theme.TermBackground
import com.opencode.chat.ui.theme.TermBorder
import com.opencode.chat.ui.theme.TermSurface
import com.opencode.chat.ui.theme.TermTextDim
import com.opencode.chat.ui.theme.TermTextPrimary

@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    var input by remember { mutableStateOf("") }
    val messages = viewModel.messages
    val isSending by viewModel.isSending

    fun trySend() {
        val text = input
        if (text.isNotBlank() && !isSending) {
            viewModel.sendMessage(text)
            input = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TermBackground)
    ) {
        // شريط علوي يشبه سطر الحالة بالطرفية
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TermSurface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "opencode",
                fontFamily = FontFamily.Monospace,
                color = TermTextPrimary,
                fontSize = MaterialTheme.typography.titleMedium.fontSize
            )
            Text(
                "جلسة متصلة",
                fontFamily = FontFamily.Monospace,
                color = TermTextDim,
                fontSize = MaterialTheme.typography.bodySmall.fontSize
            )
        }
        HorizontalDivider(color = TermBorder, thickness = 1.dp)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            items(messages) { msg ->
                TerminalMessageBlock(msg)
                Spacer(Modifier.height(14.dp))
            }
        }

        HorizontalDivider(color = TermBorder, thickness = 1.dp)

        // شريط الإدخال السفلي بنفس روح الطرفية
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(TermSurface)
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            TextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("اكتب رسالة...", fontFamily = FontFamily.Monospace, color = TermTextDim)
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
                singleLine = false,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { trySend() }),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = TermBackground,
                    unfocusedContainerColor = TermBackground,
                    focusedIndicatorColor = TermBorder,
                    unfocusedIndicatorColor = TermBorder,
                    cursorColor = TermAccentBlue
                )
            )
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${input.length} حرف",
                    fontFamily = FontFamily.Monospace,
                    color = TermTextDim,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                )
                Text(
                    if (isSending) "جارٍ الإرسال..." else "إرسال ⏎",
                    fontFamily = FontFamily.Monospace,
                    color = if (isSending) TermTextDim else TermAccentBlue,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { trySend() }
                )
            }
        }
    }
}

@Composable
private fun TerminalMessageBlock(msg: ChatMessage) {
    val isUser = msg.role == "user"

    Column(modifier = Modifier.fillMaxWidth()) {
        if (isUser) {
            Text(
                "› ${msg.text}",
                fontFamily = FontFamily.Monospace,
                color = TermTextPrimary,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize
            )
        } else {
            msg.thoughtMs?.let { ms ->
                Text(
                    "+ Thought: ${ms}ms",
                    fontFamily = FontFamily.Monospace,
                    color = TermAccentOrange,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize
                )
                Spacer(Modifier.height(4.dp))
            }
            Text(
                msg.text,
                fontFamily = FontFamily.Monospace,
                color = TermTextPrimary,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize
            )
            val meta = listOfNotNull(
                msg.agentLabel,
                msg.durationMs?.let { "${it / 1000.0}s" }
            )
            if (meta.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    meta.joinToString("  ·  "),
                    fontFamily = FontFamily.Monospace,
                    color = TermAccentBlue,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                )
            }
        }
    }
}
