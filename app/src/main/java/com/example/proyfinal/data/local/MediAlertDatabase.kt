package com.example.proyfinal.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyfinal.model.HistorialEntrada
import com.example.proyfinal.model.Medicamento
@Database(
    entities = [Medicamento::class, HistorialEntrada::class],
    version = 1,
    exportSchema = false
)
abstract class MediAlertDatabase : RoomDatabase() {

    abstract fun medicamentoDao(): MedicamentoDao
    abstract fun historialDao(): HistorialDao

    companion object {
        @Volatile
        private var INSTANCE: MediAlertDatabase? = null

        fun getDatabase(context: Context): MediAlertDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MediAlertDatabase::class.java,
                    "medialert_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}