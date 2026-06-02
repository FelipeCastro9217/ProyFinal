package com.example.proyfinal.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.proyfinal.R
import com.example.proyfinal.viewmodel.AuthState
import com.example.proyfinal.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    authViewModel: AuthViewModel,
    onRegistroExitoso: () -> Unit,
    onVolver: () -> Unit
) {
    var email      by remember { mutableStateOf("") }
    var password   by remember { mutableStateOf("") }
    var confirm    by remember { mutableStateOf("") }
    var errorLocal by remember { mutableStateOf<String?>(null) }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            authViewModel.resetState()
            onRegistroExitoso()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.register)) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value         = email,
                onValueChange = {
                    email = it
                    errorLocal = null
                    authViewModel.resetError()
                },
                label           = { Text(stringResource(R.string.email)) },
                singleLine      = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier        = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value         = password,
                onValueChange = {
                    password = it
                    errorLocal = null
                    authViewModel.resetError()
                },
                label                = { Text(stringResource(R.string.password)) },
                singleLine           = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier             = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value         = confirm,
                onValueChange = {
                    confirm = it
                    errorLocal = null
                },
                label                = { Text(stringResource(R.string.confirm_password)) },
                singleLine           = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier             = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            val errorMsg = errorLocal ?: (authState as? AuthState.Error)?.mensaje
            if (errorMsg != null) {
                Text(
                    text  = errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    errorLocal = null
                    when {
                        email.isBlank() || password.isBlank() ->
                            errorLocal = "Completa todos los campos"
                        password != confirm ->
                            errorLocal = "Las contraseñas no coinciden"
                        password.length < 6 ->
                            errorLocal = "La contraseña debe tener al menos 6 caracteres"
                        else -> authViewModel.registrar(email.trim(), password)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled  = authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.register))
                }
            }
            Spacer(Modifier.height(12.dp))

            TextButton(onClick = onVolver) {
                Text(stringResource(R.string.already_account))
            }
        }
    }
}
