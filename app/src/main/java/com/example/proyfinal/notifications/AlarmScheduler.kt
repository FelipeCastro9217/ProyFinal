package com.example.proyfinal.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.proyfinal.model.Medicamento
import java.util.Calendar

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun programar(medicamento: Medicamento) {
        val intent = Intent(context, MedicamentoBroadcastReceiver::class.java).apply {
            putExtra("medicamento_id", medicamento.id)
            putExtra("medicamento_nombre", medicamento.nombre)
            putExtra("medicamento_dosis", medicamento.dosis)
            putExtra("medicamento_hora", medicamento.horaTexto)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicamento.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, medicamento.horaHour)
            set(Calendar.MINUTE, medicamento.horaMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        // setExactAndAllowWhileIdle para que dispare exacto aunque el dispositivo esté en Doze
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun cancelar(medicamentoId: Int) {
        val intent = Intent(context, MedicamentoBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            medicamentoId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}