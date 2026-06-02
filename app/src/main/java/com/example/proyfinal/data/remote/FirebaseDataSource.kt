package com.example.proyfinal.data.remote


import com.example.proyfinal.model.Medicamento
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseDataSource {

    private val db = FirebaseFirestore.getInstance()

    // Sube un medicamento a Firestore (sincronización en la nube)
    suspend fun guardarMedicamento(medicamento: Medicamento) {
        val data = hashMapOf(
            "nombre"     to medicamento.nombre,
            "dosis"      to medicamento.dosis,
            "frecuencia" to medicamento.frecuencia,
            "horaTexto"  to medicamento.horaTexto,
            "horaHour"   to medicamento.horaHour,
            "horaMinute" to medicamento.horaMinute,
            "userId"     to medicamento.userId,
            "activo"     to medicamento.activo
        )
        db.collection("medicamentos")
            .document("${medicamento.userId}_${medicamento.id}")
            .set(data)
            .await()
    }

    // Elimina (marca inactivo) en Firestore
    suspend fun eliminarMedicamento(userId: String, medicamentoId: Int) {
        db.collection("medicamentos")
            .document("${userId}_${medicamentoId}")
            .update("activo", false)
            .await()
    }

    // Obtiene medicamentos del usuario desde Firestore (para restaurar en otro dispositivo)
    suspend fun getMedicamentosRemoto(userId: String): List<Map<String, Any>> {
        val snapshot = db.collection("medicamentos")
            .whereEqualTo("userId", userId)
            .whereEqualTo("activo", true)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.data }
    }
}