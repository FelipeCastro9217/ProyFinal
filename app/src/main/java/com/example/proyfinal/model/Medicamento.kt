package com.example.proyfinal.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicamentos")
data class Medicamento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val dosis: String,
    val frecuencia: String,
    val horaTexto: String,
    val horaHour: Int,
    val horaMinute: Int,
    val userId: String = "",
    val activo: Boolean = true
)

