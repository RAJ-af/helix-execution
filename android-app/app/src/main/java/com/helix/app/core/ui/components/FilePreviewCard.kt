package com.helix.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helix.app.core.ui.theme.*

@Composable
fun FilePreviewCard(
    fileName: String,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.Small),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(Radius.Medium),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(Spacing.Medium)) {
            Text(
                text = fileName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(Spacing.Small))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.Small))
                    .background(SurfaceVariant)
                    .padding(Spacing.Small)
            ) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 10,
                    fontSize = 12.sp
                )
            }
        }
    }
}
