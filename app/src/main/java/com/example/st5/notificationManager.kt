package com.example.st5

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
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
    fun sendNotification(channelId: String, icon: Int, title: String, message: String, typee: Int, idd: Long, notify: Int) {
        Log.v("sendNotification", "$channelId, $icon, $title, $message, $typee, $idd, $notify")
        val delay = Intent(context, itemDelay::class.java)
        delay.putExtra("type", typee)
        delay.putExtra("idd", idd)
        delay.putExtra("notif", notify)

        val skip = Intent(context, itemSkip::class.java)
        skip.putExtra("type", typee)
        skip.putExtra("idd", idd)
        skip.putExtra("notif", notify)

        val go = Intent(context, perfilmain::class.java)

        val pDelay = PendingIntent.getBroadcast(context, notify, delay, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val pSkip = PendingIntent.getBroadcast(context, notify, skip, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val pGo = PendingIntent.getBroadcast(context, notify, go, PendingIntent.FLAG_IMMUTABLE)

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
