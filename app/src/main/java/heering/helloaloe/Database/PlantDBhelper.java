package heering.helloaloe.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Matthew on 11/29/2014.
 */

public class PlantDBhelper extends SQLiteOpenHelper{

    private static final String LOGTAG = "USERPLANTS";
    private static final String DATABASE_NAME = "userPlants.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_PLANTS = "plants";
    public static final String COLUMN_ID = "plantID";
    public static final String COLUMN_PLANT_TYPE = "plantType";
    public static final String COLUMN_PLANT_NICKNAME = "plantNickName";
    public static final String COLUMN_PLANT_SCHEDULE = "plantSchedule";
    public static final String COLUMN_PLANT_LASTWATERED = "plantLastWatered";
    public static final String COLUMN_IMAGE = "plantImage";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_PLANTS
                    + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PLANT_TYPE + " TEXT, " +
                    COLUMN_PLANT_NICKNAME + " TEXT, " +
                    COLUMN_PLANT_SCHEDULE + " INT, " +
                    COLUMN_PLANT_LASTWATERED + " TEXT, " +
                    COLUMN_IMAGE + " TEXT"
                    +  ")";


    public PlantDBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        Log.i(LOGTAG, "Table has been created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLANTS);
        onCreate(db);
    }
}
