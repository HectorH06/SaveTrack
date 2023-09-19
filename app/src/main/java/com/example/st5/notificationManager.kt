package com.example.st5

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class notificationManager(private val context: Context) {

    private val generalChannelId = "General"
    private val recordatoriosChannelId = "Recordatorios"
    private val gruposChannelId = "Grupos"

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        createNotificationChannel(
            generalChannelId,
            "Notificaciones Generales",
            "Estado de ahorro y actualización del dólar",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        createNotificationChannel(
            recordatoriosChannelId,
            "Recordatorios",
            "Pagos y eventos",
            NotificationManager.IMPORTANCE_HIGH
        )

        createNotificationChannel(
            gruposChannelId,
            "Grupos",
            "Información relevante de tus grupos",
            NotificationManager.IMPORTANCE_LOW
        )
    }

    private fun createNotificationChannel(
        channelId: String,
        name: String,
        descriptionText: String,
        importance: Int
    ) {
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    fun sendNotification(channelId: String, icon: Int, title: String, message: String, type: Int, id: Long, notify: Int) {
        val delay = Intent(context, itemDelay::class.java).apply {
            putExtra("type", type)
            putExtra("id", id)
        }
        val skip = Intent(context, itemSkip::class.java).apply {
            putExtra("type", type)
            putExtra("id", id)
        }
        val go = Intent(context, perfilmain::class.java)

        val pDelay = PendingIntent.getBroadcast(context, 1, delay, PendingIntent.FLAG_IMMUTABLE)
        val pSkip = PendingIntent.getBroadcast(context, 2, skip, PendingIntent.FLAG_IMMUTABLE)
        val pGo = PendingIntent.getBroadcast(context, 3, go, PendingIntent.FLAG_IMMUTABLE)

        val builder = when (channelId) {
            "General" -> {
                NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
            }
            "Recordatorios" -> {
                NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .addAction(R.drawable.ic_delay, "Posponer", pDelay)
                    .addAction(R.drawable.ic_skip, "Omitir", pSkip)
            }
            "Grupos" -> {
                NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .addAction(R.drawable.ic_go, "Ir", pGo)
            }
            else -> {
                NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            }
        }

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(notify, builder.build())
        }
    }
}
