package com.example.proyfinal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyfinal.data.local.MediAlertDatabase
import com.example.proyfinal.data.remote.FirebaseDataSource
import com.example.proyfinal.model.Medicamento
import com.example.proyfinal.notifications.AlarmScheduler
import com.example.proyfinal.repository.MediAlertRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MedicamentoUiState(
    val medicamentos: List<Medicamento> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null,
    val guardadoExitoso: Boolean = false
)

class MedicamentoViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MediAlertDatabase.getDatabase(application)
    private val repo = MediAlertRepository(
        db.medicamentoDao(),
        db.historialDao(),
        FirebaseDataSource()
    )
    private val alarmScheduler = AlarmScheduler(application)

    private val _uiState = MutableStateFlow(MedicamentoUiState())
    val uiState: StateFlow<MedicamentoUiState> = _uiState.asStateFlow()

    fun cargarMedicamentos(userId: String) {
        viewModelScope.launch {
            repo.getMedicamentos(userId)
                .catch { e -> _uiState.update { it.copy(error = e.message) } }
                .collect { lista ->
                    _uiState.update { it.copy(medicamentos = lista, cargando = false) }
                }
        }
    }

    fun agregarMedicamento(
        nombre: String,
        dosis: String,
        frecuencia: String,
        horaTexto: String,
        horaHour: Int,
        horaMinute: Int,
        userId: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            try {
                val med = Medicamento(
                    nombre = nombre,
                    dosis = dosis,
                    frecuencia = frecuencia,
                    horaTexto = horaTexto,
                    horaHour = horaHour,
                    horaMinute = horaMinute,
                    userId = userId
                )
                val nuevoId = repo.agregarMedicamento(med)
                // Programa la alarma con el ID real asignado por Room
                alarmScheduler.programar(med.copy(id = nuevoId.toInt()))
                _uiState.update { it.copy(cargando = false, guardadoExitoso = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = e.message) }
            }
        }
    }

    fun eliminarMedicamento(medicamento: Medicamento) {
        viewModelScope.launch {
            alarmScheduler.cancelar(medicamento.id)
            repo.eliminarMedicamento(medicamento)
        }
    }

    fun resetGuardado() {
        _uiState.update { it.copy(guardadoExitoso = false) }
    }

    fun resetError() {
        _uiState.update { it.copy(error = null) }
    }
}