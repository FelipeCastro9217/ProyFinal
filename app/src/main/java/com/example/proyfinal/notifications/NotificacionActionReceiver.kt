package com.example.proyfinal.notifications

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.proyfinal.data.local.MediAlertDatabase
import com.example.proyfinal.model.HistorialEntrada
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class NotificacionActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medicamentoId    = intent.getIntExtra("medicamento_id", 0)
        val nombre           = intent.getStringExtra("medicamento_nombre") ?: ""
        val dosis            = intent.getStringExtra("medicamento_dosis") ?: ""

        // Cierra la notificación
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(medicamentoId)

        when (intent.action) {
            "ACTION_TOMADO" -> {
                // Guarda en historial local
                val horaActual = LocalTime.now()
                    .format(DateTimeFormatter.ofPattern("hh:mm a"))
                val hoy = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

                val db = MediAlertDatabase.getDatabase(context)
                CoroutineScope(Dispatchers.IO).launch {
                    db.historialDao().insertar(
                        HistorialEntrada(
                            medicamentoId = medicamentoId,
                            medicamentoNombre = nombre,
                            dosis = dosis,
                            horaRegistrada = horaActual,
                            fecha = hoy,
                            tomado = true
                        )
                    )
                }
            }

            "ACTION_POSPONER" -> {
                // Re-programa la alarma 15 minutos después
                val nuevoTiempo = System.currentTimeMillis() + 15 * 60 * 1000L

                val nuevoIntent = Intent(context, MedicamentoBroadcastReceiver::class.java).apply {
                    putExtra("medicamento_id", medicamentoId)
                    putExtra("medicamento_nombre", nombre)
                    putExtra("medicamento_dosis", dosis)
                    putExtra("medicamento_hora", intent.getStringExtra("medicamento_hora"))
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context, medicamentoId + 3000, nuevoIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val alarmManager =
                    context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nuevoTiempo,
                    pendingIntent
                )
            }
        }
    }
}