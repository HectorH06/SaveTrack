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
    private val newsChannelId = "Recordatorios"
    private val updatesChannelId = "Grupos"

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
            newsChannelId,
            "Recordatorios",
            "Pagos y eventos",
            NotificationManager.IMPORTANCE_HIGH
        )

        createNotificationChannel(
            updatesChannelId,
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
    fun sendNotification(channelId: String, icon: Int, title: String, message: String, type: Int, id: Long) {
        val delay = Intent(context, itemDelay::class.java)
        delay.putExtra("type", type)
        delay.putExtra("id", id)
        val pDelay = PendingIntent.getBroadcast(context, 0, delay, PendingIntent.FLAG_UPDATE_CURRENT)
        val skip = Intent(context, itemSkip::class.java)
        skip.putExtra("type", type)
        skip.putExtra("id", id)
        val pSkip = PendingIntent.getBroadcast(context, 0, skip, PendingIntent.FLAG_UPDATE_CURRENT)
        val go = Intent(context, perfilmain::class.java)
        val pGo = PendingIntent.getBroadcast(context, 0, go, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = when (channelId) {
            "General" -> {
                NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            }
            "Recordatorios" -> {
                NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
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
            notify(0, builder.build())
        }
    }
}
