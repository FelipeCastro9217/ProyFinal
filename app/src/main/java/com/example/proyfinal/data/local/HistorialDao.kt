package com.example.proyfinal.data.local

import androidx.room.*
import com.example.proyfinal.model.HistorialEntrada
import kotlinx.coroutines.flow.Flow

@Dao
interface HistorialDao {


    @Query("SELECT * FROM historial ORDER BY fecha DESC, horaRegistrada DESC")
    fun getHistorial(): Flow<List<HistorialEntrada>>

    @Query("SELECT * FROM historial WHERE fecha = :fecha ORDER BY horaRegistrada DESC")
    fun getHistorialPorFecha(fecha: String): Flow<List<HistorialEntrada>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(entrada: HistorialEntrada)

    // Calcula % de adherencia en los últimos 7 días
    @Query("""
        SELECT 
            CASE WHEN COUNT(*) = 0 THEN 0 
            ELSE ROUND(SUM(CASE WHEN tomado = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(*))
            END
        FROM historial
        WHERE fecha >= :fechaInicio
    """)
    suspend fun getAdherenciaSemanal(fechaInicio: String): Int

}