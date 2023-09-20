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
    private val eventosChannelId = "Eventos"
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
            eventosChannelId,
            "Eventos",
            "Pagos y eventos",
            NotificationManager.IMPORTANCE_HIGH
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

        val montos = Intent(context, indexmandados::class.java)
        val pMontos = PendingIntent.getBroadcast(context, 4, montos, PendingIntent.FLAG_IMMUTABLE)
        val eventos = Intent(context, finanzasEventos::class.java)
        val pEventos = PendingIntent.getBroadcast(context, 4, eventos, PendingIntent.FLAG_IMMUTABLE)

        val builder = when (channelId) {
            "General" -> {
                NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
            }
            "Recordatorios" -> {
                NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .setContentIntent(pMontos)
                    .addAction(R.drawable.ic_delay, "Posponer", pDelay)
                    .addAction(R.drawable.ic_skip, "Omitir", pSkip)
            }
            "Eventos" -> {
                NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pEventos)
                    .addAction(R.drawable.ic_delay, "Posponer", pDelay)
                    .addAction(R.drawable.ic_skip, "Omitir", pSkip)
            }
            "Grupos" -> {
                NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .addAction(R.drawable.ic_go, "Ir", pGo)
            }
            else -> {
                NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
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
