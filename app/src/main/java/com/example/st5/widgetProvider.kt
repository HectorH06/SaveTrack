package com.example.st5

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

class widgetProvider : AppWidgetProvider() {
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
