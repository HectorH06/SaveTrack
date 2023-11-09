package com.example.st5.widgetConfig

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import androidx.appcompat.app.AppCompatActivity
import com.example.st5.R
import com.example.st5.databinding.WidgetContainerBinding

class activityWidgetRedirect : AppCompatActivity() {
    lateinit var binding: WidgetContainerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, widgetProviderRedirect::class.java)
        intent.action = "android.appwidget.action.APPWIDGET_UPDATE"

        val pendingIntent = PendingIntent.getBroadcast(this, 30000, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val views = RemoteViews("com.example.st5", R.layout.item_widgetredirect)
        views.setOnClickPendingIntent(R.id.fastAddM, pendingIntent)

        val appWidgetManager = AppWidgetManager.getInstance(this)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(ComponentName(this, widgetProviderRedirect::class.java))
        appWidgetManager.updateAppWidget(appWidgetIds, views)

        val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds)
        setResult(RESULT_OK, resultValue)

        Log.v("Widget creado", "PARA AÑADIR MONTO")

        finish()
    }
}