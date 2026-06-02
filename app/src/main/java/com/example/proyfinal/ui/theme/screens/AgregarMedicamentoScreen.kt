package com.example.proyfinal.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.proyfinal.R
import com.example.proyfinal.viewmodel.MedicamentoViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarMedicamentoScreen(
    medicamentoViewModel: MedicamentoViewModel,
    userId: String,
    onGuardado: () -> Unit,
    onVolver: () -> Unit
) {
    val uiState by medicamentoViewModel.uiState.collectAsState()

    var nombre      by remember { mutableStateOf("") }
    var dosis       by remember { mutableStateOf("") }
    var frecuencia  by remember { mutableStateOf("Diaria") }
    var horaHour    by remember { mutableStateOf(8) }
    var horaMinute  by remember { mutableStateOf(0) }
    var showTimePicker by remember { mutableStateOf(false) }
    var errorLocal  by remember { mutableStateOf<String?>(null) }

    val frecuencias = listOf("Diaria", "Cada 8h", "Semanal")

    // Navega de vuelta cuando se guarda exitosamente
    LaunchedEffect(uiState.guardadoExitoso) {
        if (uiState.guardadoExitoso) {
            medicamentoViewModel.resetGuardado()
            onGuardado()
        }
    }

    val horaTexto = String.format(
        Locale.getDefault(),
        "%02d:%02d %s",
        if (horaHour % 12 == 0) 12 else horaHour % 12,
        horaMinute,
        if (horaHour < 12) "AM" else "PM"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_medication)) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // Nombre
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text(stringResource(R.string.medication_name)) },
                placeholder = { Text(stringResource(R.string.medication_name_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Dosis
            OutlinedTextField(
                value = dosis,
                onValueChange = { dosis = it },
                label = { Text(stringResource(R.string.dose)) },
                placeholder = { Text(stringResource(R.string.dose_hint)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Frecuencia - chips seleccionables
            Text(
                text = stringResource(R.string.frequency),
                style = MaterialTheme.typography.labelLarge
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                frecuencias.forEach { opcion ->
                    FilterChip(
                        selected = frecuencia == opcion,
                        onClick = { frecuencia = opcion },
                        label = { Text(opcion) }
                    )
                }
            }

            // Hora
            Text(
                text = stringResource(R.string.time),
                style = MaterialTheme.typography.labelLarge
            )
            OutlinedButton(
                onClick = { showTimePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Schedule, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(horaTexto)
            }

            // Error
            errorLocal?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            uiState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            // Botón guardar
            Button(
                onClick = {
                    errorLocal = null
                    if (nombre.isBlank() || dosis.isBlank()) {
                        errorLocal = "Completa nombre y dosis"
                    } else {
                        medicamentoViewModel.agregarMedicamento(
                            nombre    = nombre.trim(),
                            dosis     = dosis.trim(),
                            frecuencia = frecuencia,
                            horaTexto  = horaTexto,
                            horaHour   = horaHour,
                            horaMinute = horaMinute,
                            userId     = userId
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.cargando
            ) {
                if (uiState.cargando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.save_medication))
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = horaHour,
            initialMinute = horaMinute,
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text(stringResource(R.string.time)) },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    horaHour   = timePickerState.hour
                    horaMinute = timePickerState.minute
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            }
        )
    }
}
