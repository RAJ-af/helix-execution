package com.helix.app.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.helix.app.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToChat: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("HELIX", fontWeight = FontWeight.Bold, letterSpacing = 2.sp) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Background
                )
            )
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = onNavigateToSearch,
                containerColor = Primary,
                contentColor = OnPrimary,
                shape = RoundedCornerShape(Radius.ExtraLarge)
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(32.dp))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Spacing.Medium)
        ) {
            item {
                Spacer(modifier = Modifier.height(Spacing.Medium))
                Text("Recent Conversations", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(Spacing.Medium))
            }

            items(5) { index ->
                ConversationItem(
                    title = "Conversation #$index",
                    preview = "Last message from AI about research...",
                    onClick = { onNavigateToChat(index.toString()) }
                )
                Spacer(modifier = Modifier.height(Spacing.Small))
            }
        }
    }
}

@Composable
fun ConversationItem(title: String, preview: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(Radius.Medium)
    ) {
        Column(modifier = Modifier.padding(Spacing.Medium)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(preview, style = MaterialTheme.typography.bodyMedium, color = Secondary)
        }
    }
}
