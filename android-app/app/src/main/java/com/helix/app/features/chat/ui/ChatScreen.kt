package com.helix.app.features.chat.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.helix.app.core.ui.theme.*
import com.helix.app.features.chat.viewmodel.ChatEvent
import com.helix.app.features.chat.viewmodel.ChatViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversationId: String,
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ChatEvent.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    LaunchedEffect(state.value.messages.size, state.value.streamingText) {
        if (state.value.messages.isNotEmpty() || state.value.streamingText.isNotEmpty()) {
            listState.animateScrollToItem(listState.layoutInfo.totalItemsCount)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Chat", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(Spacing.Medium),
                verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
            ) {
                items(state.value.messages) { message ->
                    MessageBubble(
                        content = message.content,
                        isUser = message.role == "user"
                    )
                }

                if (state.value.isStreaming) {
                    item {
                        MessageBubble(
                            content = state.value.streamingText,
                            isUser = false,
                            isStreaming = true
                        )
                    }
                }
            }

            ChatInputBar(
                text = inputText,
                onTextChange = { inputText = it },
                onSend = {
                    viewModel.sendAndStream(inputText)
                    inputText = ""
                },
                isLoading = state.value.isSending
            )
        }
    }
}

@Composable
fun MessageBubble(content: String, isUser: Boolean, isStreaming: Boolean = false) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (isUser) Primary else Surface,
            contentColor = if (isUser) OnPrimary else Primary,
            shape = RoundedCornerShape(
                topStart = Radius.Large,
                topEnd = Radius.Large,
                bottomStart = if (isUser) Radius.Large else 4.dp,
                bottomEnd = if (isUser) 4.dp else Radius.Large
            )
        ) {
            Row(modifier = Modifier.padding(horizontal = Spacing.Medium, vertical = Spacing.Small)) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (isStreaming) {
                   // Typing cursor animation could be added here
                }
            }
        }
    }
}

@Composable
fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        color = Surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(Spacing.Medium).navigationBarsPadding().imePadding(),
            verticalAlignment = Alignment.Bottom
        ) {
            TextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message Helix...", color = Secondary) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                maxLines = 5
            )

            IconButton(
                onClick = onSend,
                enabled = text.isNotBlank() && !isLoading,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Primary,
                    disabledContentColor = Secondary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = Primary)
                } else {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}
