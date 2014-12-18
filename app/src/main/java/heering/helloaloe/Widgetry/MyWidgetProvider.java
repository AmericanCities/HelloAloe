package heering.helloaloe.Widgetry;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import heering.helloaloe.HomeScreen;
import heering.helloaloe.PlantDataSource;
import heering.helloaloe.R;

/**
 * Created by Matt on 12/13/2014.
 */
public class MyWidgetProvider  extends AppWidgetProvider {

    private static final String LOGTAG = "USERPLANTS";


    @Override
    public void onUpdate(Context context,
                         AppWidgetManager appWidgetManager,final int[] appWidgetIds) {
        Log.i(LOGTAG, "Creating Widget");
//        how many of this widget is displayed on this device?
          final int N = appWidgetIds.length;
//        loop through all the displayed widgets
          for (int i = 0; i < N; i++) {
           Intent serviceIntent = new Intent(context, WidgetService.class);
           serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
           serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

           RemoteViews remoteViews = new RemoteViews("heering.helloaloe", heering.helloaloe.R.layout.mywidget);

           // Setting the adapter for listview of the widget
           remoteViews.setRemoteAdapter(heering.helloaloe.R.id.widgetListView, serviceIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
//         update the widget by calling our update method
          }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    /*called when widget deleted from host*/
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i(LOGTAG, "App Deleted");
    }

    /*called when widget created for first time*/
    @Override
    public void onEnabled(Context context) {
        Log.i(LOGTAG, "onEnabled");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName("heering.helloaloe",MyWidgetBroadcastReceiver.class.getName()),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /*called when last instance of widget deleted from host*/
    @Override
    public void onDisabled(Context context) {
        Log.i(LOGTAG, "onDisabled");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName("heering.helloaloe",MyWidgetBroadcastReceiver.class.getName()),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    /*our method to update the widget*/
    static void updateOurWidget(Context context, AppWidgetManager appWidgetManager,
                                int widgetId) {
        Log.i(LOGTAG, "updating widget");
//            set up the pending intent for the button to open a new activity
        Intent intent = new Intent(context, HomeScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mywidget);
       // views.setOnClickPendingIntent(R.id.buttonWidget, pendingIntent);
        // Tell the widget manager to update the widget
        appWidgetManager.updateAppWidget(widgetId, views);
    }

}
