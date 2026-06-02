package com.example.proyfinal.ui.theme.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.proyfinal.R
import com.example.proyfinal.util.LocaleManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    userEmail: String,
    onVolver: () -> Unit
) {
    val context     = LocalContext.current
    val currentLang = remember { LocaleManager.getSavedLanguage(context) }
    var selectedLang by remember { mutableStateOf(currentLang) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile)) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.account_section),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = userEmail, onValueChange = {},
                label = { Text(stringResource(R.string.email)) },
                readOnly = true, modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            Text(stringResource(R.string.language_section),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary)
            Text(stringResource(R.string.language_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            listOf("es" to "Español", "en" to "English").forEach { (code, label) ->
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = selectedLang == code, onClick = { selectedLang = code })
                    Spacer(Modifier.width(8.dp))
                    Text(label, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    LocaleManager.setLanguage(context, selectedLang)
                    // Recrear la Activity para que Compose aplique el nuevo idioma
                    (context as? Activity)?.recreate()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_language))
            }
        }
    }
}