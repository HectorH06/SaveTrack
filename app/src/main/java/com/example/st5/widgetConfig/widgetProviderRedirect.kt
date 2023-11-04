package com.example.st5.widgetConfig

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.st5.Index
import com.example.st5.R
import com.example.st5.database.Stlite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class widgetProviderRedirect : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == "android.appwidget.action.APPWIDGET_UPDATE") {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(
                    context,
                    widgetProviderRedirect::class.java
                )
            )
            onUpdate(context, appWidgetManager, appWidgetIds)
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
            var komodo = false
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    val assetsDao = Stlite.getInstance(context).getAssetsDao()

                    val mode = assetsDao.getTheme()
                    komodo = mode != 0
                }
            }
            Thread.sleep(300)
            val intent = Intent(context, Index::class.java)
            intent.putExtra("isDarkMode", komodo)
            intent.putExtra("currentView", 2)
            intent.putExtra("fragToGo", 4)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

            val views = RemoteViews("com.example.st5", R.layout.item_widgetredirect)
            views.setOnClickPendingIntent(R.id.fastAddM, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
