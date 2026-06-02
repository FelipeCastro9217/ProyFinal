package com.example.proyfinal.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginExitoso: () -> Unit,
    onIrARegistro: () -> Unit
) {
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            authViewModel.resetState()
            onLoginExitoso()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text  = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text  = stringResource(R.string.app_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(40.dp))

        OutlinedTextField(
            value         = email,
            onValueChange = {
                email = it
                authViewModel.resetError()   // limpia el error al escribir
            },
            label         = { Text(stringResource(R.string.email)) },
            singleLine    = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier      = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value         = password,
            onValueChange = {
                password = it
                authViewModel.resetError()   // limpia el error al escribir
            },
            label               = { Text(stringResource(R.string.password)) },
            singleLine          = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions     = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier            = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))

        if (authState is AuthState.Error) {
            Text(
                text  = (authState as AuthState.Error).mensaje,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    authViewModel.login(email.trim(), password)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled  = authState !is AuthState.Loading
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color       = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(stringResource(R.string.login))
            }
        }
        Spacer(Modifier.height(12.dp))

        TextButton(onClick = onIrARegistro) {
            Text(stringResource(R.string.no_account))
        }
    }
}
