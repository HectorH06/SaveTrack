package com.example.st5
import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

class widgetService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "android.appwidget.action.APPWIDGET_UPDATE") {
            val appWidgetManager = AppWidgetManager.getInstance(this)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(this, widgetProvider::class.java)
            )
            for (appWidgetId in appWidgetIds) {
                incrementCount(this, appWidgetId)
                widgetProvider.updateAppWidget(this, appWidgetManager, appWidgetId)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): Nothing? {
        return null
    }

    companion object {
        private const val ACTION_INCREMENT = "com.example.st5.INCREMENT"

        fun newIncrementIntent(context: Context): PendingIntent {
            val intent = Intent(context, widgetService::class.java)
            intent.action = ACTION_INCREMENT
            return PendingIntent.getService(context, 1000, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        fun getCount(context: Context, appWidgetId: Int): Int {

            return 0
        }

        fun incrementCount(context: Context, appWidgetId: Int) {

        }
    }
}
