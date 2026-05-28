package com.helix.app.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.helix.app.core.ui.theme.*
import com.helix.app.features.chat.viewmodel.TaskStepUI

@Composable
fun TaskProgressCard(
    title: String,
    steps: List<TaskStepUI>
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.Small),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(Radius.Medium)
    ) {
        Column(modifier = Modifier.padding(Spacing.Medium)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(Spacing.Medium))
            steps.forEach { step ->
                StepItem(step)
            }
        }
    }
}

@Composable
fun StepItem(step: TaskStepUI) {
    val color by animateColorAsState(
        when (step.status) {
            "completed" -> Color.Green
            "running" -> Primary
            else -> Secondary
        }
    )

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (step.status == "running") {
            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = color)
        } else {
            Icon(
                if (step.status == "completed") Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(Spacing.Small))
        Text(
            text = step.title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (step.status == "pending") Secondary else Primary
        )
    }
}
