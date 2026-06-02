package com.example.proyfinal.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial")
data class HistorialEntrada(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val medicamentoId: Int,
    val medicamentoNombre: String,
    val dosis: String,
    val horaRegistrada: String,
    val fecha: String,
    val tomado: Boolean
)
