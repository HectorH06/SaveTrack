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

        if (intent.action == "android.appwidget.action.APPWIDGET_UPDATE") {
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

            val intent = widgetService.newIncrementIntent(context)
            views.setOnClickPendingIntent(R.id.fastAddW, intent)

            val currentCount = widgetService.getCount(context, appWidgetId)
            views.setTextViewText(R.id.fastVeces, currentCount.toString())

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
