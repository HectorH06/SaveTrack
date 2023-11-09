package com.example.st5.widgetConfig

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.example.st5.R
import com.example.st5.database.Stlite
import com.example.st5.models.Widgets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class widgetProviderGasto : AppWidgetProvider() {
    private var montoIdToShow: Long = -1
    private var widgetId: Int = -1

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == "android.appwidget.action.APPWIDGET_UPDATE") {
            montoIdToShow = intent.getLongExtra("IDM", -1)
            widgetId = intent.getIntExtra("IDW", -1)
            if (montoIdToShow != -1L && widgetId != -1) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.IO) {
                        val widgetsDao = Stlite.getInstance(context).getWidgetsDao()
                        val nuevoWidget = Widgets(
                            idwidget = widgetId,
                            idmonto = montoIdToShow
                        )

                        if (!widgetsDao.getAllWidgetIds().contains(nuevoWidget.idwidget)) {
                            widgetsDao.insertWidget(nuevoWidget)
                        }

                        val allWidgets = widgetsDao.getAllWidgets()
                        Log.v("widget ALL", "$allWidgets")

                        val appWidgetIdArray: IntArray = intArrayOf(widgetId)
                        onUpdate(context, appWidgetManager, appWidgetIdArray)
                    }
                }
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            Log.v("widget id at update", "$appWidgetId")
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    val widgetsDao = Stlite.getInstance(context).getWidgetsDao()
                    val idMonto = widgetsDao.getIdMontoDeWidget(appWidgetId)

                    updateAppWidget(context, appWidgetManager, appWidgetId, idMonto)
                }
            }
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
            val intent = widgetServiceGasto.newIncrementIntent(context, appWidgetId, montoId)
            views.setOnClickPendingIntent(R.id.fastAddW, intent)

            widgetServiceGasto.incrementCount(context, appWidgetId)

            val veces = widgetServiceGasto.getVeces(context, appWidgetId, montoId)
            val concepto = widgetServiceGasto.getConcepto(context, appWidgetId, montoId)
            val valor = widgetServiceGasto.getValor(context, appWidgetId, montoId)
            views.setTextViewText(R.id.fastVeces, veces.toString())
            views.setTextViewText(R.id.fastConcepto, concepto)
            views.setTextViewText(R.id.fastValor, valor)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
