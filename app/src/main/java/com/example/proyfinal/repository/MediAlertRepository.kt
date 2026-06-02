package com.example.proyfinal.repository

import com.example.proyfinal.data.local.HistorialDao
import com.example.proyfinal.data.local.MedicamentoDao
import com.example.proyfinal.data.remote.FirebaseDataSource
import com.example.proyfinal.model.HistorialEntrada
import com.example.proyfinal.model.Medicamento
import kotlinx.coroutines.flow.Flow

class MediAlertRepository(
    private val medicamentoDao: MedicamentoDao,
    private val historialDao: HistorialDao,
    private val firebaseDataSource: FirebaseDataSource
) {

    // ── Medicamentos ──────────────────────────────────────────────────────────

    fun getMedicamentos(userId: String): Flow<List<Medicamento>> =
        medicamentoDao.getMedicamentosByUser(userId)

    suspend fun agregarMedicamento(medicamento: Medicamento): Long {
        val nuevoId = medicamentoDao.insertar(medicamento)
        // Sube a Firebase en segundo plano (no bloquea UI)
        try {
            firebaseDataSource.guardarMedicamento(medicamento.copy(id = nuevoId.toInt()))
        } catch (_: Exception) { /* sin conexión: se guarda solo local */ }
        return nuevoId
    }

    suspend fun actualizarMedicamento(medicamento: Medicamento) {
        medicamentoDao.actualizar(medicamento)
        try { firebaseDataSource.guardarMedicamento(medicamento) } catch (_: Exception) {}
    }

    suspend fun eliminarMedicamento(medicamento: Medicamento) {
        medicamentoDao.eliminar(medicamento.id)
        try {
            firebaseDataSource.eliminarMedicamento(medicamento.userId, medicamento.id)
        } catch (_: Exception) {}
    }

    suspend fun getMedicamentoById(id: Int): Medicamento? =
        medicamentoDao.getMedicamentoById(id)

    // ── Historial ─────────────────────────────────────────────────────────────

    fun getHistorial(): Flow<List<HistorialEntrada>> =
        historialDao.getHistorial()

    fun getHistorialPorFecha(fecha: String): Flow<List<HistorialEntrada>> =
        historialDao.getHistorialPorFecha(fecha)

    suspend fun registrarToma(entrada: HistorialEntrada) =
        historialDao.insertar(entrada)

    suspend fun getAdherenciaSemanal(fechaInicio: String): Int =
        historialDao.getAdherenciaSemanal(fechaInicio)
}