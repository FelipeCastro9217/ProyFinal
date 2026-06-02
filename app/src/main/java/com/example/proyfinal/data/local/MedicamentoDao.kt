package com.example.proyfinal.data.local

import androidx.room.*
import com.example.proyfinal.model.Medicamento
import kotlinx.coroutines.flow.Flow
@Dao
interface MedicamentoDao {
    @Query("SELECT * FROM medicamentos WHERE userId = :userId AND activo = 1")
    fun getMedicamentosByUser(userId: String): Flow<List<Medicamento>>

    @Query("SELECT * FROM medicamentos WHERE id = :id")
    suspend fun getMedicamentoById(id: Int): Medicamento?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(medicamento: Medicamento): Long

    @Update
    suspend fun actualizar(medicamento: Medicamento)

    @Query("UPDATE medicamentos SET activo = 0 WHERE id = :id")
    suspend fun eliminar(id: Int)

}