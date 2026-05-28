package com.helix.app.features.auth.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.helix.app.core.ui.components.HelixButton
import com.helix.app.core.ui.theme.Background
import com.helix.app.core.ui.theme.Secondary
import com.helix.app.core.ui.theme.Spacing
import com.helix.app.features.auth.viewmodel.AuthEvent
import com.helix.app.features.auth.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val state = viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AuthEvent.AuthSuccess -> {
                    onLoginSuccess()
                }
                is AuthEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Login") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Spacing.Large)
        ) {
            Text(
                "Welcome Back",
                style = MaterialTheme.typography.displayLarge,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Sign in to continue to Helix",
                color = Secondary,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(Spacing.Medium))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(Spacing.ExtraLarge))

            HelixButton(
                text = "Login",
                onClick = { viewModel.onLogin(email, password) },
                enabled = !state.value.isLoading && email.isNotBlank() && password.isNotBlank()
            )

            if (state.value.isLoading) {
                Spacer(modifier = Modifier.height(Spacing.Medium))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
