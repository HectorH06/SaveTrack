package com.example.st5

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class widgetProvider : AppWidgetProvider() {
    private var montoIdToShow: Long = -1

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == "ACTUALIZAR_WIDGET") {
            montoIdToShow = intent.getLongExtra("IDM", -1)
            if (montoIdToShow != -1L) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, widgetProvider::class.java))
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.item_widgetfasti)

            // Puedes agregar lógica para manejar acciones del widget aquí
            // Por ejemplo, incrementar un contador cuando se hace clic en el botón
            val intent = widgetService.newIncrementIntent(context)
            views.setOnClickPendingIntent(R.id.fastAddW, intent)

            // Actualiza el texto en el widget (en este caso, incrementa un contador)
            val currentCount = widgetService.getCount(context, appWidgetId)
            views.setTextViewText(R.id.fastVeces, currentCount.toString())

            // Actualiza el widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
