package com.example.android.cyclebuddy.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android.cyclebuddy.MainActivity;
import com.example.android.cyclebuddy.R;

/**
 * Implementation of App Widget functionality.
 */
public class BuddyWidget extends AppWidgetProvider {

    private final static String WIDGET_ICON = "widget icon";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.buddy_widget);

        Intent searchIntent = new Intent(context, MainActivity.class);
        searchIntent.putExtra(WIDGET_ICON, 1);
        PendingIntent searchPendingIntent = PendingIntent.getActivity(context, 9, searchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_search, searchPendingIntent);

        Intent offerIntent = new Intent(context, MainActivity.class);
        searchIntent.putExtra(WIDGET_ICON, 2);
        PendingIntent offerPendingIntent = PendingIntent.getActivity(context, 18, offerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_offer, offerPendingIntent);

        Intent messageIntent = new Intent(context, MainActivity.class);
        searchIntent.putExtra(WIDGET_ICON, 2);
        PendingIntent messagePendingIntent = PendingIntent.getActivity(context, 27, messageIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_messages, messagePendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

