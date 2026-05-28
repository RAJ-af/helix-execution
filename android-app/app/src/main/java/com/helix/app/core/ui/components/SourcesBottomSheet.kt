package com.helix.app.core.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.helix.app.core.data.remote.sse.SourceDto
import com.helix.app.core.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourcesBottomSheet(
    sources: List<SourceDto>,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.Medium)
                .navigationBarsPadding()
        ) {
            Text(
                "All Sources",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(Spacing.Medium))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(Spacing.Medium)
            ) {
                items(sources) { source ->
                    Column {
                        Text(source.title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        Text(source.url, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                        Text(source.snippet, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(Spacing.Huge))
        }
    }
}
