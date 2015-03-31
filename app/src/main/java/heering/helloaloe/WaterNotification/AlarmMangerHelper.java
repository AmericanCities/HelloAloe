package heering.helloaloe.WaterNotification;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import heering.helloaloe.Database.PlantDataSource;
import heering.helloaloe.FirstLaunch;
import heering.helloaloe.HomeScreen;
import heering.helloaloe.Plant;


/**
 * Created by Matt on 3/21/2015.
 */
public interface AlarmMangerHelper {
    static final String LOGTAG = "USERPLANTS";
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String ALLSSUMMER = "endlessSummer";
    public static final String SETSUMMER = "setSummer";

    class SetAlarm {
        private static PendingIntent pendingIntent;

        public static void setAlarmManager(Context context, long plantID, Boolean add) {
            PlantDataSource datasource = new PlantDataSource(context);
            datasource.open();
            SharedPreferences prefs = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE);

            boolean previouslyStarted= prefs.getBoolean(SETSUMMER,false);
            Log.i(LOGTAG, "Previously set should be true:  " + previouslyStarted);
            boolean summer = prefs.getBoolean(ALLSSUMMER,false);
            Log.i(LOGTAG, "Summer is:  " + summer);

            Plant alarmPlant = datasource.getPlant(plantID);

            // create a calendar
            // http://stackoverflow.com/questions/18197710/adding-days-to-calendar
            Calendar waterCal = Calendar.getInstance();
            int monthParsed = Integer.parseInt(alarmPlant.getPlantLastWatered().substring(5, 7)) - 1;
            int dayParsed = Integer.parseInt(alarmPlant.getPlantLastWatered().substring(8));
            int yearParesed = Integer.parseInt(alarmPlant.getPlantLastWatered().substring(0, 4));
            waterCal.set(yearParesed, monthParsed, dayParsed);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Log.i(LOGTAG, "last Watered was " + sdf.format(waterCal.getTime()));

            //If user has a winter and month is before April or after September water on winter scheudle
            //else water on summer scheudle
                if ((!summer)&& ((monthParsed < 4) || (monthParsed > 9))) {
                    waterCal.add(Calendar.DAY_OF_YEAR, alarmPlant.getWinterSchedule());
                    Log.i(LOGTAG, "Applying Winter Schedule days: " + alarmPlant.getWinterSchedule());
                }
                else {
                    waterCal.add(Calendar.DAY_OF_YEAR, alarmPlant.getSummerSchedule());
                    Log.i(LOGTAG, "Applying Summer Schedule days: " + alarmPlant.getSummerSchedule());
               }

            waterCal.set(Calendar.HOUR_OF_DAY, 18);
            waterCal.set(Calendar.MINUTE, 0);
            waterCal.set(Calendar.SECOND, 0);

            Log.i(LOGTAG, "Alarm is set for: " + sdf.format(waterCal.getTime()));

            // hack to cast plantID to int for intent request code...  plantID shouldn't get too big
            // Request code needed to cancel alarm service
            int plantIDint = (int) (long) plantID;

            Intent alarmIntent = new Intent(context, NotificationBroadcastReceiver.class);
            alarmIntent.putExtra("message", alarmPlant.getPlantType());
            alarmIntent.putExtra("plantIdentifier", alarmPlant.getPlantID());
            pendingIntent = PendingIntent.getBroadcast(context, plantIDint, alarmIntent,0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            long time = waterCal.getTimeInMillis();
            //alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            if (add) {
                alarmManager.set(AlarmManager.RTC, time, pendingIntent);
                Log.i(LOGTAG, "adding Alarm with time: " + time);
            }
             else {
                try {
                    alarmManager.cancel(pendingIntent);
                    Log.i(LOGTAG, "Canceled Alarm with time: " + time);
                } catch (Exception e) {
                    Log.e(LOGTAG, "AlarmManager update was not canceled. " + e.toString());
                }
                try {
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(plantIDint);
                } catch (Exception e) {
                    Log.e(LOGTAG, "Couldn't find a current notificaiton " + e.toString());
                }
            }
                datasource.close();
        }

    }

    class ChangeLastWateredDate {

        public static void setAlarmManager(Context context, long plantID) {
            PlantDataSource datasource = new PlantDataSource(context);
            datasource.open();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar today = Calendar.getInstance();
            String date = sdf.format(today.getTime());
            Log.i(LOGTAG, "------------------------");
            Log.i(LOGTAG, "Updating the last Watered date to: " + date);
            datasource.updatePlantLastWatered(plantID,date);
            datasource.close();
        }
    }

}
