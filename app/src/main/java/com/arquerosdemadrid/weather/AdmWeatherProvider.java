package com.arquerosdemadrid.weather;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

public class AdmWeatherProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int count = appWidgetIds.length;

        for (int i = 0; i < count; i++) {
            int widgetId = appWidgetIds[i];

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.adm_weather);           

            try {

                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet("http://icarus.live:5000/api/status");
                remoteViews.setTextViewText(R.id.textRain, "3");
                HttpResponse response = httpclient.execute(httpget);
                remoteViews.setTextViewText(R.id.textRain, "4");

                if(response.getStatusLine().getStatusCode()==200){
                    String server_response = EntityUtils.toString(response.getEntity());
                    JSONArray json = new JSONArray(server_response);
                    remoteViews.setTextViewText(R.id.textRain, "5");

                    for (int y = 0; y < json.length(); y++) {
                        if (json.getJSONObject(y).getString("name").equals("temp")) {
                            remoteViews.setTextViewText(R.id.textTemperature, json.getJSONObject(y).getString("value") + "ºC");
                        } else if (json.getJSONObject(y).getString("name").equals("humid")) {
                            remoteViews.setTextViewText(R.id.textHumidity, json.getJSONObject(y).getString("value") + "%");
                        } else if (json.getJSONObject(y).getString("name").equals("rain")) {
                            if (Float.valueOf(json.getJSONObject(y).getString("value")) > 600){
                                remoteViews.setTextViewText(R.id.textRain, context.getResources().getString(R.string.rain_status_strong));
                            } else if (Float.valueOf(json.getJSONObject(y).getString("value")) > 400){
                                remoteViews.setTextViewText(R.id.textRain, context.getResources().getString(R.string.rain_status_normal));
                            } else if (Float.valueOf(json.getJSONObject(y).getString("value")) > 200){
                                remoteViews.setTextViewText(R.id.textRain, context.getResources().getString(R.string.rain_status_weak));
                            } else {
                                remoteViews.setTextViewText(R.id.textRain, context.getResources().getString(R.string.rain_status_null));
                            }
                        }
                    }

                } else {
                    remoteViews.setTextViewText(R.id.textTemperature, "---");
                    remoteViews.setTextViewText(R.id.textHumidity, "---");
                    remoteViews.setTextViewText(R.id.textRain, "---");
                }
            } catch (Exception e) {
                remoteViews.setTextViewText(R.id.textTemperature, "---");
                remoteViews.setTextViewText(R.id.textHumidity, "---");
                remoteViews.setTextViewText(R.id.textRain, "---");
            }
                        
            Intent intent = new Intent(context, AdmWeatherProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.actionButton, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
