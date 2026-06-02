package com.example.proyfinal.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.proyfinal.data.local.MediAlertDatabase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = MediAlertDatabase.getDatabase(context)
        val scheduler = AlarmScheduler(context)

        CoroutineScope(Dispatchers.IO).launch {
            val medicamentos = db.medicamentoDao()
                .getMedicamentosByUser(userId)
                .first()
            medicamentos.forEach { scheduler.programar(it) }
        }
    }
}