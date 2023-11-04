package com.example.st5.widgetConfig

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.example.st5.R

class widgetProviderIngreso : AppWidgetProvider() {
    private var montoIdToShow: Long = -1

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == "android.appwidget.action.APPWIDGET_UPDATE") {
            montoIdToShow = intent.getLongExtra("IDM", -1)
            if (montoIdToShow != -1L) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(context, widgetProviderIngreso::class.java))
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
            updateAppWidget(context, appWidgetManager, appWidgetId, montoIdToShow)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            montoId: Long
        ) {
            val views = RemoteViews(context.packageName, R.layout.item_widgetfast)
            Log.v("widget Id", "$appWidgetId")
            val intent = widgetServiceIngreso.newIncrementIntent(context, montoId)
            views.setOnClickPendingIntent(R.id.fastAddW, intent)

            widgetServiceIngreso.incrementCount(context, appWidgetId, montoId)

            val veces = widgetServiceIngreso.getVeces(context, appWidgetId, montoId)
            val concepto = widgetServiceIngreso.getConcepto(context, appWidgetId, montoId)
            val valor = widgetServiceIngreso.getValor(context, appWidgetId, montoId)
            views.setTextViewText(R.id.fastVeces, veces.toString())
            views.setTextViewText(R.id.fastConcepto, concepto)
            views.setTextViewText(R.id.fastValor, valor)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
