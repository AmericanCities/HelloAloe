package heering.helloaloe.WaterNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import heering.helloaloe.CreateNotificationActivity;
import heering.helloaloe.HomeScreen;
import heering.helloaloe.PlantDetails;
import heering.helloaloe.R;

/**
 * Created by Matt on 3/9/2015.
 */

// Below is the code I rifted on
// http://papers.ch/android-schedule-a-notification/
// Also helpful
//http://www.vogella.com/tutorials/AndroidNotifications/article.html
//http://developer.android.com/guide/topics/ui/notifiers/notifications.html

//http://javatechig.com/android/android-notification-example-using-notificationcompat

// https://developer.android.com/training/scheduling/alarms.html
// Need to register for reboot status - so as to reset alarms.
// https://developer.android.com/reference/android/app/AlarmManager.html#RTC
// http://developer.android.com/guide/topics/ui/notifiers/notifications.html
// http://papers.ch/android-schedule-a-notification/


public class NotificationBroadcastReceiver extends BroadcastReceiver {
    private static final String LOGTAG = "USERPLANTS";

    @Override
    public void onReceive(Context context, Intent paramIntent) {

        // Request the notification manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a new intent which will be fired if you click on the notification
        Intent intent = new Intent(context,HomeScreen.class);

        // Attach the intent to a pending intent
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the notification
        Notification noti = new Notification.Builder(context)
                .setContentTitle("Your plant may be thirsty")
                .setContentText("Subject").setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_launcher, "water", pendingIntent)
                .addAction(R.drawable.ic_launcher, "More", pendingIntent)
                .addAction(R.drawable.ic_launcher, "water", pendingIntent).build();
             // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, noti);


//        String message = intent.getExtras().getString("message");
//        Log.i(LOGTAG, "Got to the Broadcast Receiver and the message is: " + message);
//        Intent pIntent = new Intent(context,CreateNotificationActivity.class);
//        startActivity(PIntent);


// Intent service1 = new Intent(context,CreateNotificationActivity.class);
//        context.startService(service1);
//        Notification noti = new Notification.Builder(context)
//                .setContentTitle("Your plant may be thirsty")
//                .setContentText("Subject").setSmallIcon(R.drawable.ic_launcher)
//                .setContentIntent(pIntent)
//                .addAction(R.drawable.ic_launcher, "water", pIntent)
//                .addAction(R.drawable.ic_launcher, "More", pIntent)
//                .addAction(R.drawable.ic_launcher, "water", pIntent).build();
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        // hide the notification after its selected
//        noti.flags |= Notification.FLAG_AUTO_CANCEL;
//        notificationManager.notify(0, noti);
//      }
    }
}

