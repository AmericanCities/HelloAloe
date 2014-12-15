package heering.helloaloe;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Matthew on 11/19/2014.
 *
 * This section allows a user to add a plant
 *
 */
public class AddPlant extends Activity implements View.OnClickListener {

    public static final String LOGTAG = "USERPLANTS";
    public EditText plantNickNameTXTMSG;
    public int plantSched = 0;
    public TextView displayDPfDate;
    public PlantDataSource datasource;
    public String filename;
    public String plantType;
    public boolean photoTaken;


    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_plant);
        // hide keyboard on startup
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Create View variables
        plantNickNameTXTMSG = (EditText) findViewById(R.id.plantNickNameET);
        plantNickNameTXTMSG.setImeOptions(EditorInfo.IME_ACTION_DONE); //add done to keyboard
        // DatePicker plantLastWateredDP = (DatePicker)(findViewById(R.id.datePicker));

    
        ArrayList <Plant> preDefinedPlants = new ArrayList<Plant>();
        try {
            String result = loadJSONFromAsset();
            Log.i(LOGTAG,"got Json Asset");
            JSONObject root = new JSONObject("{"+result+"}");
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
        }


        Spinner spinner = (Spinner) findViewById(R.id.spinnerPlantType);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<Plant> Plantadapter = new ArrayAdapter<Plant>(
                this,android.R.layout.simple_spinner_item, preDefinedPlants);
        // Specify the layout to use when the list of choices appears
        Plantadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(Plantadapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                Plant plantSelection = (Plant) arg0.getItemAtPosition(position);
                if (plantSelection != null) {
                    plantType = plantSelection.getPlantType();
                    plantSched =plantSelection.getPlantSchedule();
                    if (!photoTaken) {
                        String fileDrawable = plantSelection.getPlantImage();
                        filename = "android.resource://heering.helloaloe/drawable/" + fileDrawable;
                        ImageView cameraShot = (ImageView) findViewById(R.id.takePicture);
                        cameraShot.setImageURI(Uri.parse(filename));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        addButtonListeners();
    }

    public String loadJSONFromAsset() {
        String json;
        try {
            Log.i(LOGTAG,"Now get Json Asset");
            InputStream is = getAssets().open("planttypesjson.txt");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    private void addButtonListeners() {

        TextView datePickerBtn = (TextView)findViewById(R.id.displayDPdate);
        if (datePickerBtn !=null) {
            datePickerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    displayDPfDate = (TextView)findViewById(R.id.displayDPdate);
                    DatePickerFragment datePicker = new DatePickerFragment(displayDPfDate);
                    datePicker.show(getFragmentManager(), "datePicker");
                }
            });
        }

        ImageView takePicture = (ImageView)findViewById(R.id.takePicture);
        if (takePicture != null)
            takePicture.setOnClickListener(this);

        Button saveData = (Button) findViewById(R.id.savePlant);
        if (saveData != null)
            saveData.setOnClickListener(this);
    }



    @Override
    public void onClick(View selectedView) {
        if (selectedView.getId() == R.id.takePicture) {
            dispatchTakePictureIntent();
        }
        if (selectedView.getId() == R.id.savePlant) {
            savePlant();
        }
    }

    private void savePlant() {
        String plantNickName= plantNickNameTXTMSG.getText().toString();
        datasource = new PlantDataSource(this);
        datasource.open();
        Plant plant = new Plant();
        plant.setPlantType(plantType);
        plant.setPlantNickName(plantNickName);
        plant.setPlantSchedule(plantSched);
        plant.setPlantLastWatered(DatePickerFragment.sqlFormattedDate);
        plant.setPlantImage(filename);
        Log.i(LOGTAG,"Now add plant record");
        plant = datasource.create(plant);
        Log.i(LOGTAG, "Plant created with id " + plant.getPlantID());
        Log.i(LOGTAG, "The plant type is " + plantType);
        Log.i(LOGTAG, "The plant nick name is " + plantNickName);
        Log.i(LOGTAG, "The plant schedule is " + plantSched);
        Log.i(LOGTAG, "The last Watered date is " + DatePickerFragment.sqlFormattedDate);
        Log.i(LOGTAG, "The picture path is " + filename);
    }

//----------------------
// TAKE PHOTO
//----------------------
    static final int REQUEST_IMAGE_CAPTURE = 509;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            filename=saveToInternalSorage(imageBitmap);
            ImageView cameraShot = (ImageView) findViewById(R.id.takePicture);
            cameraShot.setImageURI(Uri.parse(filename));
            photoTaken = true;
        }}

    private String saveToInternalSorage(Bitmap bitmapImage){
            //http://stackoverflow.com/questions/17674634/saving-images-to-internal-memory-in-android
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            // Create imageDir
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + ".jpg";
            File myPath = new File(directory,imageFileName);
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(myPath);

               // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return (directory.getAbsolutePath() +"/" +imageFileName);
    }


    public static class DatePickerFragment extends DialogFragment
implements DatePickerDialog.OnDateSetListener {
        public static String sqlFormattedDate;
        public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        TextView displayDate;

        public DatePickerFragment(TextView textview)
        {
            displayDate = textview;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sqlFormattedDate = sdf.format(c.getTime());
            displayDate.setText( MONTHS[month] + " " + day);
        }

    }
}
