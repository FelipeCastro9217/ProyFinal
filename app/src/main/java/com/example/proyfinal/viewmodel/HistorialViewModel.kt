package com.example.proyfinal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyfinal.data.local.MediAlertDatabase
import com.example.proyfinal.data.remote.FirebaseDataSource
import com.example.proyfinal.model.HistorialEntrada
import com.example.proyfinal.repository.MediAlertRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class HistorialUiState(
    val entradas: List<HistorialEntrada> = emptyList(),
    val adherenciaSemanal: Int = 0,
    val cargando: Boolean = false
)

class HistorialViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MediAlertDatabase.getDatabase(application)
    private val repo = MediAlertRepository(
        db.medicamentoDao(),
        db.historialDao(),
        FirebaseDataSource()
    )

    private val _uiState = MutableStateFlow(HistorialUiState())
    val uiState: StateFlow<HistorialUiState> = _uiState.asStateFlow()

    init {
        cargarHistorial()
    }

    private fun cargarHistorial() {
        viewModelScope.launch {
            repo.getHistorial().collect { lista ->
                _uiState.update { it.copy(entradas = lista) }
            }
        }
        viewModelScope.launch {
            val hace7dias = LocalDate.now().minusDays(7)
                .format(DateTimeFormatter.ISO_LOCAL_DATE)
            val adherencia = repo.getAdherenciaSemanal(hace7dias)
            _uiState.update { it.copy(adherenciaSemanal = adherencia) }
        }
    }

    fun registrarToma(
        medicamentoId: Int,
        medicamentoNombre: String,
        dosis: String,
        hora: String,
        tomado: Boolean
    ) {
        viewModelScope.launch {
            val hoy = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            repo.registrarToma(
                HistorialEntrada(
                    medicamentoId = medicamentoId,
                    medicamentoNombre = medicamentoNombre,
                    dosis = dosis,
                    horaRegistrada = hora,
                    fecha = hoy,
                    tomado = tomado
                )
            )
            // Recalcula adherencia
            val hace7dias = LocalDate.now().minusDays(7)
                .format(DateTimeFormatter.ISO_LOCAL_DATE)
            val adherencia = repo.getAdherenciaSemanal(hace7dias)
            _uiState.update { it.copy(adherenciaSemanal = adherencia) }
        }
    }
}