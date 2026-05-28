package com.helix.app.features.welcome.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helix.app.core.ui.components.HelixButton
import com.helix.app.core.ui.theme.Secondary
import com.helix.app.core.ui.theme.Spacing

@Composable
fun WelcomeScreen(onNavigateToHome: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.Large),
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = "Intelligence\nat your fingertips.",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 40.sp
        )

        Spacer(modifier = Modifier.height(Spacing.Medium))

        Text(
            text = "Experience the next generation of AI search and assistance.",
            color = Secondary,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(Spacing.Huge))

        HelixButton(
            text = "Get Started",
            onClick = onNavigateToHome
        )

        Spacer(modifier = Modifier.height(Spacing.Medium))
    }
}
