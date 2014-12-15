package heering.helloaloe;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * A BroadcastReceiver that listens for updates for the MyWidgetProvider. Starts off disabled,
 * is only enabled when there is a widget instance created,
 * - only receive notifications when we need them.
 *
 * created using Android Studio (Beta) 0.8.2
 www.101apps.co.za
 */

public class MyWidgetBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "junk";

    @Override
    public void onReceive(Context context, Intent intent) {
        // For our example, we'll also update all of the widgets when the timezone
        // changes, or the user or network sets the time.
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_TIMEZONE_CHANGED)
               || action.equals(Intent.ACTION_TIME_CHANGED)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            ComponentName thisComponent = new ComponentName(context, MyWidgetProvider.class);
            int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisComponent);

            final int N = allWidgetIds.length;
            for (int i = 0; i < N; i++) {
                MyWidgetProvider
                        .updateOurWidget(context, appWidgetManager,allWidgetIds[i]);
            }
        }
    }

}
