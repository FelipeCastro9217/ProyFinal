package com.example.proyfinal.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.proyfinal.R
import com.example.proyfinal.model.Medicamento
import com.example.proyfinal.viewmodel.MedicamentoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicamentosScreen(
    medicamentoViewModel: MedicamentoViewModel,
    userId: String,
    onAgregarClick: () -> Unit,
    onHistorialClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val uiState  by medicamentoViewModel.uiState.collectAsState()
    var busqueda by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        medicamentoViewModel.cargarMedicamentos(userId)
    }

    val medicamentosFiltrados = uiState.medicamentos.filter {
        it.nombre.contains(busqueda, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_medications)) },
                actions = {
                    IconButton(onClick = onHistorialClick) {
                        Icon(Icons.Default.History, contentDescription = stringResource(R.string.history))
                    }
                    IconButton(onClick = onPerfilClick) {
                        Icon(Icons.Default.Language, contentDescription = stringResource(R.string.profile))
                    }
                    IconButton(onClick = onCerrarSesion) {
                        Icon(Icons.Default.Logout, contentDescription = stringResource(R.string.logout))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAgregarClick) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_medication))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value         = busqueda,
                onValueChange = { busqueda = it },
                placeholder   = { Text(stringResource(R.string.search_medication)) },
                leadingIcon   = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            if (uiState.cargando) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (medicamentosFiltrados.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        stringResource(R.string.no_medications),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(medicamentosFiltrados, key = { it.id }) { med ->
                        MedicamentoCard(
                            medicamento = med,
                            onEliminar  = { medicamentoViewModel.eliminarMedicamento(med) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MedicamentoCard(
    medicamento: Medicamento,
    onEliminar: () -> Unit
) {
    var confirmarEliminar by remember { mutableStateOf(false) }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text  = medicamento.nombre,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text  = "${medicamento.dosis} · ${medicamento.frecuencia}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint     = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text  = medicamento.horaTexto,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            IconButton(onClick = { confirmarEliminar = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title   = { Text(stringResource(R.string.delete_medication_title)) },
            text    = { Text(stringResource(R.string.delete_medication_confirm, medicamento.nombre)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    onEliminar()
                }) { Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}
