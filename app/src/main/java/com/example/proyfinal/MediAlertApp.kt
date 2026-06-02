package com.example.proyfinal


import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.proyfinal.data.local.MediAlertDatabase
import com.example.proyfinal.notifications.MedicamentoBroadcastReceiver
import com.example.proyfinal.util.LocaleManager

class MediAlertApp : Application() {

    val database: MediAlertDatabase by lazy {
        MediAlertDatabase.getDatabase(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.wrapContext(base))
    }

    override fun onCreate() {
        super.onCreate()
        crearCanalNotificaciones()
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MedicamentoBroadcastReceiver.CHANNEL_ID,
                "Recordatorios de medicamentos",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones para recordar tomar medicamentos"
                enableVibration(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}