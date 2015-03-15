package heering.helloaloe.WaterNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import heering.helloaloe.HomeScreen;
import heering.helloaloe.R;

/**
 * Created by Matt on 3/13/2015.
 */
public class WaterButtonReceiver extends BroadcastReceiver {
    private static final String LOGTAG = "USERPLANTS";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOGTAG, "Got to the Water Receiver ");

        // Request the notification manager
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a new intent which will be fired if you click on the notification
        Intent intentHome = new Intent(context,HomeScreen.class);

        // Attach the intent to a pending intent
        PendingIntent headHome = PendingIntent.getActivity(context, 0, intentHome, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews customNotification = new RemoteViews("heering.helloaloe",
                R.layout.customnotification);

        customNotification.setTextViewText(R.id.plantMessage, "Your plant is happy");

        customNotification.setViewVisibility(R.id.progressBar, View.VISIBLE);

        Bitmap aloeLogo= BitmapFactory.decodeResource(context.getResources(),
                R.drawable.aloelogolarge);

        // Create the notification with NotificationCompat.Builder
        final Notification noti = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.customnotificationticker))
                .setContentText("Swipe down, or tap for the App")
                .setSmallIcon(R.drawable.wateringcan)
                .setLargeIcon(aloeLogo)
                .setAutoCancel(true)
                .build();



        noti.bigContentView = customNotification;
        noti.bigContentView.setOnClickPendingIntent(R.id.notifyEditButton, headHome);

        // Start a lengthy operation in a background thread
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        int incr;
                        // Do the "lengthy" operation 20 times
                        for (incr = 0; incr <= 15; incr+=1) {
                            // Sets the progress indicator to a max value, the
                            // current completion percentage, and "determinate"
                            // state
                            noti.bigContentView.setProgressBar(R.id.progressBar, 15, incr, false);
                            // Displays the progress bar for the first time.
                            notificationManager.notify(4, noti);
                            // Sleeps the thread, simulating an operation
                            // that takes time
                            try {
                                // Sleep for .2 seconds
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                Log.d(LOGTAG, "sleep failure");
                            }
                        }
                        // When the loop is finished, updates the notification
                        notificationManager.cancel(4);
                    }
                }
// Starts the thread by calling the run() method in its Runnable
        ).start();




        // Fire off logic to update the DB plant watering date

    }



    }
