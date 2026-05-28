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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.helix.app.core.data.local.prefs.TokenManager
import com.helix.app.core.ui.theme.Primary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class TokenViewModel @Inject constructor(
    val tokenManager: TokenManager
) : ViewModel()

@Composable
fun SplashScreen(
    onNavigateToWelcome: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: TokenViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        delay(2000)
        val token = viewModel.tokenManager.accessToken.first()
        if (token != null) {
            onNavigateToHome()
        } else {
            onNavigateToWelcome()
        }
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
