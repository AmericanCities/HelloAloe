package heering.helloaloe;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by Matt on 12/13/2014.
 */
public class MyWidgetProvider  extends AppWidgetProvider {

    private static final String TAG = "USERPLANTS";

    /*    most important callback
    called when widget added and for subsequent updates
    if you have a configuration activity -
    not called when widget added but is called for subsequent updates */
    //public void onUpdate(Context context,AppWidgetManager appWidgetManager, int[] appWidgetIds)

    @Override
    public void onUpdate(Context context,
                         AppWidgetManager appWidgetManager,final int[] appWidgetIds) {
        Log.i(TAG, "onUpdate");
//        how many of this widget is displayed on this device?
          final int N = appWidgetIds.length;
//        loop through all the displayed widgets
         for (int i = 0; i < N; i++) {
             int appWidgetId = appWidgetIds[i];

            //get the saved widget label from the saved preferences
            // file for the widget with this ID
//            String widgetLabel = MyWidgetConfigureActivity
//                    .loadWidgetLabelPreference(context, appWidgetId);
//            update the widget by calling our update method
//            updateOurWidget(context, appWidgetManager, appWidgetId, widgetLabel);
            updateOurWidget(context, appWidgetManager, appWidgetId);
        }
    }


    /*called when widget deleted from host*/
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i(TAG, "App Deleted");
    }

    /*called when widget created for first time*/
    @Override
    public void onEnabled(Context context) {
        Log.i(TAG, "onEnabled");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName("heering.helloaloe",MyWidgetBroadcastReceiver.class.getName()),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /*called when last instance of widget deleted from host*/
    @Override
    public void onDisabled(Context context) {
        Log.i(TAG, "onDisabled");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName("heering.helloaloe",MyWidgetBroadcastReceiver.class.getName()),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    /*our method to update the widget*/
    static void updateOurWidget(Context context, AppWidgetManager appWidgetManager,
                                int widgetId) {
        Log.i(TAG, "updating widget");
//            set up the pending intent for the button to open a new activity
        Intent intent = new Intent(context, HomeScreen.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mywidget);
       // views.setTextViewText(R.id.textViewWidgetLabel, widgetLabel);
       // views.setOnClickPendingIntent(R.id.buttonWidget, pendingIntent);
        // Tell the widget manager to update the widget
        appWidgetManager.updateAppWidget(widgetId, views);
    }

}
