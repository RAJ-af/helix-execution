package com.helix.app.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.helix.app.core.ui.theme.Spacing

data class AIModelInfo(
    val id: String,
    val name: String,
    val provider: String,
    val capability: String,
    val speed: String
)

val availableModels = listOf(
    AIModelInfo("claude", "Claude 3.5 Sonnet", "Anthropic", "Coding & Analysis", "Fast"),
    AIModelInfo("gemini", "Gemini 1.5 Flash", "Google", "Quick Search", "Instant"),
    AIModelInfo("openrouter", "OpenRouter (Auto)", "Multi-Cloud", "General Purpose", "Variable")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSelectorBottomSheet(
    onModelSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.fillMaxWidth().padding(Spacing.Medium).navigationBarsPadding()) {
            Text("Select AI Model", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(Spacing.Medium))
            LazyColumn {
                items(availableModels) { model ->
                    ListItem(
                        headlineContent = { Text(model.name, fontWeight = FontWeight.SemiBold) },
                        supportingContent = { Text("${model.provider} • ${model.capability}") },
                        trailingContent = { Badge { Text(model.speed) } },
                        modifier = Modifier.clickable { onModelSelected(model.id) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(Spacing.Huge))
        }
    }
}
