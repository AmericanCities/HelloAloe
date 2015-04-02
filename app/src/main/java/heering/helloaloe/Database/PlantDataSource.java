package heering.helloaloe.Database;

/**
 * Created by Matthew on 11/29/2014.
 */

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import heering.helloaloe.ListViewItem;
import heering.helloaloe.Plant;


public class PlantDataSource {

    public static final String LOGTAG = "USERPLANTS";
    SQLiteOpenHelper dbhelper;
    SQLiteDatabase database;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String ALLSSUMMER = "endlessSummer";
    public static final String SETSUMMER = "setSummer";

    private static final String[] allColumns = {
            PlantDBhelper.COLUMN_ID,
            PlantDBhelper.COLUMN_PLANT_TYPE,
            PlantDBhelper.COLUMN_PLANT_NICKNAME,
            PlantDBhelper.COLUMN_PLANT_SUMMER_SCHEDULE,
            PlantDBhelper.COLUMN_PLANT_WINTER_SCHEDULE,
            PlantDBhelper.COLUMN_PLANT_LASTWATERED,
            PlantDBhelper.COLUMN_IMAGE    };

    public PlantDataSource(Context context){
        dbhelper = new PlantDBhelper(context);
    }

    public void open(){
        Log.i(LOGTAG, "Database opened");
        database = dbhelper.getWritableDatabase();
    }

    public void close(){
        Log.i(LOGTAG, "Database closed");
        dbhelper.close();
    }

    public Plant create(Plant plant){
        ContentValues values = new ContentValues();
        values.put(PlantDBhelper.COLUMN_PLANT_TYPE, plant.getPlantType());
        values.put(PlantDBhelper.COLUMN_PLANT_NICKNAME, plant.getPlantNickName());
        values.put(PlantDBhelper.COLUMN_PLANT_SUMMER_SCHEDULE, plant.getSummerSchedule());
        values.put(PlantDBhelper.COLUMN_PLANT_WINTER_SCHEDULE, plant.getWinterSchedule());
        values.put(PlantDBhelper.COLUMN_PLANT_LASTWATERED, plant.getPlantLastWatered());
        values.put(PlantDBhelper.COLUMN_IMAGE, plant.getPlantImage());
        long insertid = database.insert(PlantDBhelper.TABLE_PLANTS,null,values);
        plant.setPlantID(insertid);
        return plant;
    }

    public void deletePlant(Long plantID){
        String whereClause = "plantID" + "=?";
        String[] whereArgs = new String[] { String.valueOf(plantID) };
        database.delete(PlantDBhelper.TABLE_PLANTS,whereClause,whereArgs);
    }


    public Plant updatePlant (Plant plant){
        ContentValues values = new ContentValues();
        values.put(PlantDBhelper.COLUMN_PLANT_TYPE, plant.getPlantType());
        values.put(PlantDBhelper.COLUMN_PLANT_NICKNAME, plant.getPlantNickName());
        values.put(PlantDBhelper.COLUMN_PLANT_SUMMER_SCHEDULE, plant.getSummerSchedule());
        values.put(PlantDBhelper.COLUMN_PLANT_WINTER_SCHEDULE, plant.getWinterSchedule());
        values.put(PlantDBhelper.COLUMN_PLANT_LASTWATERED, plant.getPlantLastWatered());
        values.put(PlantDBhelper.COLUMN_IMAGE, plant.getPlantImage());
        database.update(PlantDBhelper.TABLE_PLANTS,values,"plantId" + "="+plant.getPlantID(),null);
        return plant;
    }


    public void updatePlantLastWatered (long plantID, String date){
        Log.i(LOGTAG, "Got to the db for plant ID: " + plantID + " date: " + date);
        ContentValues values = new ContentValues();
        values.put(PlantDBhelper.COLUMN_PLANT_LASTWATERED, date);
        database.update(PlantDBhelper.TABLE_PLANTS,values,"plantId" + "="+plantID,null);
    }


    public Plant getPlant (long plantID) {
        Plant gotPlant = new Plant();
        Log.i(LOGTAG, "got here with plantID " + plantID);
        String dbQuery = "SELECT * FROM plants WHERE plantID = ?";
        String plantIDString = Long.toString(plantID);
        //dbQuery,null
        Cursor c = database.rawQuery(dbQuery, new String[] {plantIDString});
        Log.i(LOGTAG, "Returned from db " + c.getCount() + " rows");
        if (c.getCount()>0  && c.moveToFirst()) {
            gotPlant.setPlantType(c.getString(c.getColumnIndex(PlantDBhelper.COLUMN_PLANT_TYPE)));
            gotPlant.setPlantNickName(c.getString(c.getColumnIndex(PlantDBhelper.COLUMN_PLANT_NICKNAME)));
            gotPlant.setSummerSchedule(c.getInt(c.getColumnIndex(PlantDBhelper.COLUMN_PLANT_SUMMER_SCHEDULE)));
            gotPlant.setWinterSchedule(c.getInt(c.getColumnIndex(PlantDBhelper.COLUMN_PLANT_WINTER_SCHEDULE)));
            gotPlant.setPlantImage(c.getString(c.getColumnIndex(PlantDBhelper.COLUMN_IMAGE)));
            gotPlant.setPlantLastWatered(c.getString(c.getColumnIndex(PlantDBhelper.COLUMN_PLANT_LASTWATERED)));
            gotPlant.setPlantID(plantID);
            Log.i(LOGTAG, "found plant");
        }
        return gotPlant;
    }


    public ArrayList getListItems(Context context){
        open();
        ArrayList plantList = new ArrayList<ListViewItem>();

        Cursor cursor = database.query(PlantDBhelper.TABLE_PLANTS,allColumns,
                null,null,null,null,null);
        Log.i(LOGTAG, "Returned from db " + cursor.getCount() + " rows");
        int water=0;
        String lastWatered;
        int summerWater;
        int winterWater;
        if (cursor.getCount()>0) {
            while (cursor.moveToNext()){
                // Figure out when the next date to water based on last watering and plant schedule
                // This is way too much code! :(
                lastWatered = cursor.getString(cursor.getColumnIndex(PlantDBhelper.COLUMN_PLANT_LASTWATERED));
                summerWater = Integer.parseInt(cursor.getString(cursor.getColumnIndex(PlantDBhelper.COLUMN_PLANT_SUMMER_SCHEDULE)));
                winterWater = Integer.parseInt(cursor.getString(cursor.getColumnIndex(PlantDBhelper.COLUMN_PLANT_WINTER_SCHEDULE)));

                Calendar nextWateringCal = Calendar.getInstance();
                //YYYY-MM-DD
                int monthParsed = Integer.parseInt(lastWatered.substring(5, 7)) - 1;
                int dayParsed = Integer.parseInt(lastWatered.substring(8));
                int yearParesed = Integer.parseInt(lastWatered.substring(0, 4));

                nextWateringCal.set(yearParesed, monthParsed, dayParsed);
                nextWateringCal.set(Calendar.HOUR_OF_DAY, 0);
                nextWateringCal.set(Calendar.MINUTE, 0);
                nextWateringCal.set(Calendar.SECOND, 0);

                SharedPreferences prefs = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE);
                boolean summer = prefs.getBoolean(ALLSSUMMER,false);
                if ((!summer)&& ((monthParsed < 4) || (monthParsed > 9))) {
                    nextWateringCal.add(Calendar.DAY_OF_YEAR, winterWater);
                }
                else {
                    nextWateringCal.add(Calendar.DAY_OF_YEAR, summerWater);
                }

                long nextWaterTime = nextWateringCal.getTimeInMillis();
                Date nxtW = new Date(nextWaterTime);

                Calendar todayDate = Calendar.getInstance();
                todayDate.set(Calendar.HOUR_OF_DAY, 0);
                todayDate.set(Calendar.MINUTE, 0);
                todayDate.set(Calendar.SECOND, 0);
                long todayTime = todayDate.getTimeInMillis();
                Date tdy = new Date(todayTime);
                int remainingDays = (int)((nxtW.getTime()-tdy.getTime())/(1000 * 60 * 60 * 24));

                plantList.add(new ListViewItem(
                         cursor.getString(cursor.getColumnIndex(PlantDBhelper.COLUMN_PLANT_TYPE)),
                         cursor.getString(cursor.getColumnIndex(PlantDBhelper.COLUMN_PLANT_NICKNAME)),
                         cursor.getString(cursor.getColumnIndex(PlantDBhelper.COLUMN_IMAGE)),
                         cursor.getLong(cursor.getColumnIndex(PlantDBhelper.COLUMN_ID)),
                         remainingDays));
                }

        }
        Log.i(LOGTAG, "Created plantList for ListView with " + plantList.size());
        close();
        return plantList;
    }


    public List<Plant> findAll(){
        List<Plant> plants = new ArrayList<Plant>();

        Cursor cursor = database.query(PlantDBhelper.TABLE_PLANTS,allColumns,
                null,null,null,null,null);
        Log.i(LOGTAG, "Returned from db " + cursor.getCount() + " rows");

        if (cursor.getCount()>0) {
            while (cursor.moveToNext()){
                Plant plant = new Plant();
                plant.setPlantID(cursor.getLong(cursor.getColumnIndex(PlantDBhelper.COLUMN_ID)));
                plant.setPlantType(cursor.getString(cursor.getColumnIndex(PlantDBhelper.COLUMN_PLANT_TYPE)));
                plant.setPlantNickName(cursor.getString(cursor.getColumnIndex(PlantDBhelper.COLUMN_PLANT_NICKNAME)));
                plant.setSummerSchedule(cursor.getInt(cursor.getColumnIndex(PlantDBhelper.COLUMN_PLANT_SUMMER_SCHEDULE)));
                plant.setWinterSchedule(cursor.getInt(cursor.getColumnIndex(PlantDBhelper.COLUMN_PLANT_WINTER_SCHEDULE)));
                plant.setPlantLastWatered(cursor.getString(cursor.getColumnIndex(PlantDBhelper.COLUMN_PLANT_LASTWATERED)));
                plant.setPlantImage(cursor.getString(cursor.getColumnIndex(PlantDBhelper.COLUMN_IMAGE)));
                plants.add(plant);
            }
        }
        return plants;
    }
}