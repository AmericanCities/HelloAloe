package heering.helloaloe.Database;

/**
 * Created by Matthew on 11/29/2014.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

import heering.helloaloe.ListViewItem;
import heering.helloaloe.Plant;


public class PlantDataSource {

    public static final String LOGTAG = "USERPLANTS";
    SQLiteOpenHelper dbhelper;
    SQLiteDatabase database;

    private static final String[] allColumns = {
            PlantDBhelper.COLUMN_ID,
            PlantDBhelper.COLUMN_PLANT_TYPE,
            PlantDBhelper.COLUMN_PLANT_NICKNAME,
            PlantDBhelper.COLUMN_PLANT_SCHEDULE,
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
        values.put(PlantDBhelper.COLUMN_PLANT_SCHEDULE, plant.getPlantSchedule());
        values.put(PlantDBhelper.COLUMN_PLANT_LASTWATERED, plant.getPlantLastWatered());
        values.put(PlantDBhelper.COLUMN_IMAGE, plant.getPlantImage());
        long insertid = database.insert(PlantDBhelper.TABLE_PLANTS,null,values);
        plant.setPlantID(insertid);
        return plant;
    }

    public void deletePlant(Long plantID){
        String whereClause = "plantID" + "=?";
        String dbQuery = "SELECT * FROM plants WHERE plantID = ?";
        String[] whereArgs = new String[] { String.valueOf(plantID) };
        database.delete(PlantDBhelper.TABLE_PLANTS,whereClause,whereArgs);
    }


    public Plant updatePlant (Plant plant){
        ContentValues values = new ContentValues();
        values.put(PlantDBhelper.COLUMN_PLANT_TYPE, plant.getPlantType());
        values.put(PlantDBhelper.COLUMN_PLANT_NICKNAME, plant.getPlantNickName());
        values.put(PlantDBhelper.COLUMN_PLANT_SCHEDULE, plant.getPlantSchedule());
        values.put(PlantDBhelper.COLUMN_PLANT_LASTWATERED, plant.getPlantLastWatered());
        values.put(PlantDBhelper.COLUMN_IMAGE, plant.getPlantImage());
        database.update(PlantDBhelper.TABLE_PLANTS,values,"plantId" + "="+plant.getPlantID(),null);
        return plant;
    }


    public Plant getPlant (long plantID) {
        Plant gotPlant = new Plant();
        Log.i(LOGTAG, "got here with plantID" + plantID);
        String dbQuery = "SELECT * FROM plants WHERE plantID = ?";
        String plantIDString = Long.toString(plantID);
        //dbQuery,null
        Cursor c = database.rawQuery(dbQuery, new String[] {plantIDString});
        Log.i(LOGTAG, "Returned from db " + c.getCount() + " rows");
        if (c.getCount()>0  && c.moveToFirst()) {
            gotPlant.setPlantType(c.getString(c.getColumnIndex(PlantDBhelper.COLUMN_PLANT_TYPE)));
            gotPlant.setPlantNickName(c.getString(c.getColumnIndex(PlantDBhelper.COLUMN_PLANT_NICKNAME)));
            gotPlant.setPlantSchedule(c.getInt(c.getColumnIndex(PlantDBhelper.COLUMN_PLANT_SCHEDULE)));
            gotPlant.setPlantImage(c.getString(c.getColumnIndex(PlantDBhelper.COLUMN_IMAGE)));
            gotPlant.setPlantLastWatered(c.getString(c.getColumnIndex(PlantDBhelper.COLUMN_PLANT_LASTWATERED)));
            gotPlant.setPlantID(plantID);
            Log.i(LOGTAG, "found plant");
        }
        return gotPlant;
    }


    public ArrayList getListItems(){
        open();
        ArrayList plantList = new ArrayList<ListViewItem>();
        Cursor cursor = database.query(PlantDBhelper.TABLE_PLANTS,allColumns,
                null,null,null,null,null);
        Log.i(LOGTAG, "Returned from db " + cursor.getCount() + " rows");

        if (cursor.getCount()>0) {
            while (cursor.moveToNext()){
                plantList.add(new ListViewItem(
                         cursor.getString(cursor.getColumnIndex(PlantDBhelper.COLUMN_PLANT_TYPE)),
                         cursor.getString(cursor.getColumnIndex(PlantDBhelper.COLUMN_PLANT_NICKNAME)),
                         cursor.getString(cursor.getColumnIndex(PlantDBhelper.COLUMN_IMAGE)),
                          cursor.getLong(cursor.getColumnIndex(PlantDBhelper.COLUMN_ID))));
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
                plant.setPlantSchedule(cursor.getInt(cursor.getColumnIndex(PlantDBhelper.COLUMN_PLANT_SCHEDULE)));
                plant.setPlantLastWatered(cursor.getString(cursor.getColumnIndex(PlantDBhelper.COLUMN_PLANT_LASTWATERED)));
                plant.setPlantImage(cursor.getString(cursor.getColumnIndex(PlantDBhelper.COLUMN_IMAGE)));
                plants.add(plant);
            }
        }
        return plants;
    }
}