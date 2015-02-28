package heering.helloaloe.JsonReader;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import heering.helloaloe.Plant;

/**
 * Created by Matt on 1/23/2015.
 */

public class JsonPlantReader {
    public static final String LOGTAG = "USERPLANTS";
    private static String json;

    public JsonPlantReader(Context context) throws JSONException {
        try {
            Log.i(LOGTAG,"Now get Json Asset");
            AssetManager am = context.getAssets();
            InputStream is = am.open("planttypesjson.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public static ArrayList<Plant> getPreDefinedPlants() {
        ArrayList<Plant> preDefinedPlants = new ArrayList<Plant>();
        try {
            Log.i(LOGTAG, "got Json Asset");
            JSONObject root = new JSONObject("{" + json + "}");
            JSONArray jsonPlantTypes = root.getJSONArray("plants");
            for (int i = 0; i < jsonPlantTypes.length(); i++) {
                JSONObject jsonPlant = jsonPlantTypes.getJSONObject(i);
                Plant plant = new Plant();
                String jsplantName = jsonPlant.getString("plant_type");
                plant.setPlantType(jsplantName);
                String plantImage = jsonPlant.getString("image");
                plant.setPlantImage(plantImage);
                int plantSchedule = Integer.parseInt(jsonPlant.getString("water_schedule"));
                plant.setPlantSchedule(plantSchedule);
                preDefinedPlants.add(plant);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        };
        return preDefinedPlants;
    }
}