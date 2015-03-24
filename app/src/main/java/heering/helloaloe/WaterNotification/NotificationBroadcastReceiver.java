package heering.helloaloe.WaterNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import heering.helloaloe.HomeScreen;
import heering.helloaloe.R;

/**
 * Created by Matt on 3/9/2015.
 */

// Below is the code I rifted on
// http://papers.ch/android-schedule-a-notification/
// Also helpful
//http://www.vogella.com/tutorials/AndroidNotifications/article.html
//http://developer.android.com/guide/topics/ui/notifiers/notifications.html
//http://www.objc.io/issue-11/android-notifications.html
//http://stackoverflow.com/questions/12438209/handling-buttons-inside-android-notifications


//TODO: display progress bar in notificaiton when watering action occurs
// example here: http://developer.android.com/guide/topics/ui/notifiers/notifications.html#BigNotify
// https://developer.android.com/training/scheduling/alarms.html
// Need to register for reboot status - so as to reset alarms.
// https://developer.android.com/reference/android/app/AlarmManager.html#RTC
// http://developer.android.com/guide/topics/ui/notifiers/notifications.html
//http://stackoverflow.com/questions/6522792/get-list-of-active-pendingintents-in-alarmmanager


public class NotificationBroadcastReceiver extends BroadcastReceiver {
    private static final String LOGTAG = "USERPLANTS";

    @Override
    public void onReceive(Context context, Intent paramIntent) {
        String message = paramIntent.getExtras().getString("message");
        long plantID = paramIntent.getExtras().getLong("plantIdentifier");
        Log.i(LOGTAG, "Got to the Broadcast Receiver and the message is: " + message + "and the plant id is: " + plantID);
        int plantIDint = (int) (long) plantID;

        Intent waterIntent = new Intent(context, WaterButtonReceiver.class);
               waterIntent.putExtra("plantGetter", plantID);
        PendingIntent pendingWaterIntent = PendingIntent.getBroadcast(context, 0, waterIntent,PendingIntent.FLAG_CANCEL_CURRENT);

        // Request the notification manager.

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a new intent which will be fired if you click on the notification
        Intent intentHome = new Intent(context,HomeScreen.class);

        // Attach the intent to a pending intent
        PendingIntent headHome = PendingIntent.getActivity(context, 0, intentHome, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews customNotification = new RemoteViews("heering.helloaloe",
            R.layout.customnotification);

        customNotification.setTextViewText(R.id.plantMessage, "Your " + message + " is thirsty");
        customNotification.setViewVisibility(R.id.progressBar, View.INVISIBLE);

        Bitmap aloeLogo= BitmapFactory.decodeResource(context.getResources(),
                R.drawable.aloelogolarge);


        Notification noti = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.customnotificationticker))
                .setContentText("Swipe down, or tap for the App")
                .setSmallIcon(R.drawable.wateringcan)
                .setLargeIcon(aloeLogo)
                .setAutoCancel(true)
                .setContentIntent(headHome)
                .build();

        noti.bigContentView = customNotification;
        noti.bigContentView.setProgressBar(R.id.progressBar, 0, 0, true);
        noti.bigContentView.setOnClickPendingIntent(R.id.notifyWaterButton,pendingWaterIntent);
        noti.bigContentView.setOnClickPendingIntent(R.id.notifyEditButton, headHome);
        notificationManager.notify(plantIDint,noti);

        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

    }

    }
