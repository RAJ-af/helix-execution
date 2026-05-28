package com.helix.app.features.chat.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.helix.app.core.ui.components.*
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

    LaunchedEffect(state.value.messages.size, state.value.streamingText, state.value.taskSteps.size) {
        if (state.value.messages.isNotEmpty() || state.value.streamingText.isNotEmpty() || state.value.taskSteps.isNotEmpty()) {
            listState.animateScrollToItem(listState.layoutInfo.totalItemsCount)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Helix AI", fontWeight = FontWeight.Bold) },
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
                contentPadding = PaddingValues(Spacing.Medium)
            ) {
                items(state.value.messages) { message ->
                    MessageBubble(content = message.content, isUser = message.role == "user")
                }

                if (state.value.clarificationQuestion != null) {
                    item {
                        ClarificationUI(
                            question = state.value.clarificationQuestion!!,
                            options = state.value.clarificationOptions,
                            onOptionSelected = { /* Handle option */ }
                        )
                    }
                }

                if (state.value.isExecutingTask || state.value.taskSteps.isNotEmpty()) {
                    item {
                        TaskProgressCard(
                            title = state.value.taskTitle,
                            steps = state.value.taskSteps
                        )
                    }
                }

                if (state.value.isSearching) {
                    item { Text("Searching...", color = Secondary, style = MaterialTheme.typography.bodySmall) }
                }

                if (state.value.sources.isNotEmpty()) {
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.Small)) {
                            items(state.value.sources) { source ->
                                SourceCard(title = source.title, url = source.url, onClick = {})
                            }
                        }
                    }
                }

                if (state.value.isExecutingTool || state.value.toolOutput.isNotEmpty()) {
                    item {
                        ToolCard(
                            toolName = state.value.toolName,
                            input = state.value.toolInput,
                            output = state.value.toolOutput,
                            isExecuting = state.value.isExecutingTool
                        )
                    }
                }

                if (state.value.isStreaming) {
                    item { MessageBubble(content = state.value.streamingText, isUser = false) }
                }

                if (state.value.followUps.isNotEmpty()) {
                    item {
                        Column {
                            state.value.followUps.forEach { question ->
                                SuggestionChip(
                                    onClick = { viewModel.sendAndStream(question) },
                                    label = { Text(question) },
                                    modifier = Modifier.padding(bottom = Spacing.ExtraSmall)
                                )
                            }
                        }
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
fun MessageBubble(content: String, isUser: Boolean) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.Small),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (isUser) SurfaceVariant else Color.Transparent,
            shape = RoundedCornerShape(Radius.Medium)
        ) {
            Text(
                text = content,
                modifier = Modifier.padding(if (isUser) Spacing.Medium else 0.dp),
                style = if (isUser) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.headlineSmall,
                color = Primary
            )
        }
    }
}

@Composable
fun ChatInputBar(text: String, onTextChange: (String) -> Unit, onSend: () -> Unit, isLoading: Boolean) {
    Surface(color = Surface, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(Spacing.Medium).navigationBarsPadding().imePadding(),
            verticalAlignment = Alignment.Bottom
        ) {
            TextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask or command...", color = Secondary) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )
            IconButton(onClick = onSend, enabled = text.isNotBlank() && !isLoading) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                else Icon(Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}
