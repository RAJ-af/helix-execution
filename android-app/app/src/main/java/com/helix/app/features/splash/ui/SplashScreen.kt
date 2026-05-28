package com.helix.app.features.splash.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.helix.app.core.ui.theme.Primary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToWelcome: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onNavigateToWelcome()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "HELIX",
            color = Primary,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 8.sp
        )
    }
}
