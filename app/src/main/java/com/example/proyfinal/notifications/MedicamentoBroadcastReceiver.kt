package com.example.proyfinal.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.proyfinal.MainActivity
import com.example.proyfinal.R

class MedicamentoBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val medicamentoId    = intent.getIntExtra("medicamento_id", 0)
        val nombre           = intent.getStringExtra("medicamento_nombre") ?: "Medicamento"
        val dosis            = intent.getStringExtra("medicamento_dosis") ?: ""
        val hora             = intent.getStringExtra("medicamento_hora") ?: ""

        // Intent al abrir la notificación → abre MainActivity
        val abrirApp = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("medicamento_id", medicamentoId)
            putExtra("abrir_historial", true)
        }
        val pendingAbrirApp = PendingIntent.getActivity(
            context, medicamentoId, abrirApp,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Acción "Tomado" directamente desde la notificación
        val intentTomado = Intent(context, NotificacionActionReceiver::class.java).apply {
            action = "ACTION_TOMADO"
            putExtra("medicamento_id", medicamentoId)
            putExtra("medicamento_nombre", nombre)
            putExtra("medicamento_dosis", dosis)
            putExtra("medicamento_hora", hora)
        }
        val pendingTomado = PendingIntent.getBroadcast(
            context, medicamentoId + 1000, intentTomado,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Acción "Posponer 15 min"
        val intentPosponer = Intent(context, NotificacionActionReceiver::class.java).apply {
            action = "ACTION_POSPONER"
            putExtra("medicamento_id", medicamentoId)
            putExtra("medicamento_nombre", nombre)
            putExtra("medicamento_dosis", dosis)
            putExtra("medicamento_hora", hora)
        }
        val pendingPosponer = PendingIntent.getBroadcast(
            context, medicamentoId + 2000, intentPosponer,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_pill)
            .setContentTitle("⏰ $nombre")
            .setContentText("Hora de tomar $dosis — $hora")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingAbrirApp)
            .addAction(0, "✓ Tomado", pendingTomado)
            .addAction(0, "⏱ Posponer 15 min", pendingPosponer)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(medicamentoId, notification)
    }

    companion object {
        const val CHANNEL_ID = "medialert_channel"
    }
}
