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
import android.widget.Toast;
import org.json.JSONException;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import heering.helloaloe.Database.PlantDataSource;
import heering.helloaloe.JsonReader.JsonPlantReader;

/**
 * Created by Matthew on 11/19/2014.
 *
 * This section allows a user to add a plant
 *
 */
public class PlantDetails extends Activity implements View.OnClickListener {

    public static final String LOGTAG = "USERPLANTS";
    public EditText plantNickNameTXTMSG;
    public int plantSched = 0;
    public TextView displayDPfDate;
    public PlantDataSource datasource;
    public Spinner spinner;
    public long plantID;
    Plant knownPlant = new Plant();
    Plant workingPlant = new Plant();
    public String filename;
    public String plantType;
    public boolean photoTaken;
    public boolean newPlant;
    public Button deletePlant;
    public Button saveData;
    public ImageView cameraShot;
    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public DatePickerFragment datePicker;


    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_delete_plant);

        // Create View variables
        plantNickNameTXTMSG = (EditText) findViewById(R.id.plantNickNameET);
        plantNickNameTXTMSG.setImeOptions(EditorInfo.IME_ACTION_DONE); //add done to keyboard
        spinner = (Spinner) findViewById(R.id.spinnerPlantType);
        cameraShot = (ImageView) findViewById(R.id.takePicture);
        displayDPfDate = (TextView)findViewById(R.id.displayDPdate);
        deletePlant = (Button) findViewById(R.id.deletePlant);
        saveData = (Button) findViewById(R.id.savePlant);
        datePicker = new DatePickerFragment(displayDPfDate);

        // hides keyboard on startup
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        // Crude error validation: Pre-populate water date as today's date in case user does not specify it.
        final Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String setDate= sdf.format(c.getTime());
        workingPlant.setPlantLastWatered(setDate);



        // If this is not a new plant populate interface with existing plant details and other options
        newPlant = getIntent().getExtras().getBoolean("newPlant");
        if (!newPlant) {
            plantID = getIntent().getExtras().getLong("plantID");
            datasource = new PlantDataSource(this);
            datasource.open();
            knownPlant = datasource.getPlant(plantID);
            datasource.close();
            plantNickNameTXTMSG.setText(knownPlant.getPlantNickName());
            cameraShot.setImageURI(Uri.parse(knownPlant.getPlantImage()));
            photoTaken = true;

                Log.i(LOGTAG, "---------------------");
                Log.i(LOGTAG, "Getting plant record ");
                Log.i(LOGTAG, "---------------------");
                Log.i(LOGTAG, "Plant got with id " + knownPlant.getPlantID());
                Log.i(LOGTAG, "The plant type is " + knownPlant.getPlantType());
                Log.i(LOGTAG, "The plant schedule is " + knownPlant.getPlantSchedule());
                Log.i(LOGTAG, "The last Watered date is " + knownPlant.getPlantLastWatered());
                Log.i(LOGTAG, "The picture path is " + knownPlant.getPlantImage());
                Log.i(LOGTAG, "---------------------");

            String plantWateredDate = knownPlant.getPlantLastWatered();
            int monthParsed = Integer.parseInt(plantWateredDate.substring(5,7));
            int dayParsed = Integer.parseInt(plantWateredDate.substring(8));
            displayDPfDate.setText(MONTHS[monthParsed - 1] + " " + dayParsed);
            Log.i(LOGTAG, "displayDPDate is: " +  MONTHS[monthParsed - 1] + " " + dayParsed);
            plantSched= knownPlant.getPlantSchedule();
            deletePlant.setVisibility(View.VISIBLE);
            saveData.setText("update");
            workingPlant = knownPlant;
        }
        addListeners();
    }


    private void addListeners() {
        // spinner drop down listener
        populateSpinner();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                Plant plantSelection = (Plant) arg0.getItemAtPosition(position);
                if (plantSelection != null) {
                    plantType  = plantSelection.getPlantType();
                    workingPlant.setPlantType(plantType);
                    plantSched = plantSelection.getPlantSchedule();
                    workingPlant.setPlantSchedule(plantSched);
                    if (!photoTaken) {
                        String fileDrawable = plantSelection.getPlantImage();
                        filename = "android.resource://heering.helloaloe/drawable/" + fileDrawable;
                        cameraShot.setImageURI(Uri.parse(filename));
                        workingPlant.setPlantImage(filename);
                    }
                }
            }                                                                  // end onItemSelected
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });                                                                   // end on OnItemSelectedListener

        //Date Picker button listener
        TextView datePickerBtn = (TextView)findViewById(R.id.displayDPdate);
        if (datePickerBtn !=null) {
            datePickerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //DatePickerFragment datePicker = new DatePickerFragment(displayDPfDate);
                    datePicker.show(getFragmentManager(), "datePicker");
                }
            });
        }

        //Take Picture button listener
        ImageView takePicture = (ImageView)findViewById(R.id.takePicture);
        if (takePicture != null)
            takePicture.setOnClickListener(this);

        //Save Plant button Listner
        if (saveData != null)
            saveData.setOnClickListener(this);

        //Delete Plant button listener
        if (deletePlant != null)
            deletePlant.setOnClickListener(this);
    }

    private void populateSpinner(){
        // Create an ArrayAdapter using the string array and a default spinner layout
        try {
            new JsonPlantReader(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList <Plant> preDefinedPlants;
        preDefinedPlants=JsonPlantReader.getPreDefinedPlants();

        ArrayAdapter<Plant> plantAdapter = new ArrayAdapter<Plant>(
                this,android.R.layout.simple_spinner_item, preDefinedPlants);
        // Specify the layout to use when the list of choices appears
        plantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(plantAdapter);

        // If known plant set the default spinner drop down.
        if(!newPlant){
            String knownPlantType = knownPlant.getPlantType();
            int xx = 0;
            int found = -1;
            // Iterate over the List to find what position the known-plant-Type is in the drop-down
            // In order to have it selected
            for (Plant plant : preDefinedPlants) {
                if(plant.getPlantType().equals(knownPlantType)) {
                    found = xx;
                    spinner.setSelection(found);
                }
                    else xx++;
            }
        }
    }

    @Override
    public void onClick(View selectedView) {
        if (selectedView.getId() == R.id.takePicture) {
            dispatchTakePictureIntent();
        }
        if (selectedView.getId() == R.id.savePlant) {
            Boolean updatePlant = true;
            saveUpdateDeletePlant(updatePlant);
        }

        if (selectedView.getId() == R.id.deletePlant) {
            Boolean updatePlant = false;
            saveUpdateDeletePlant(updatePlant);
        }
    }


   //---------------
   //Save Plant
   //---------------
    private void saveUpdateDeletePlant(boolean updatePlant) {
        String updateAction = "added";
        if (DatePickerFragment.sqlFormattedDate != null){
            workingPlant.setPlantLastWatered(DatePickerFragment.sqlFormattedDate);
        }

        workingPlant.setPlantNickName(plantNickNameTXTMSG.getText().toString());
        datasource = new PlantDataSource(this);
        datasource.open();
        if (newPlant){
            workingPlant = datasource.create(workingPlant);
        }
        else {
            if (updatePlant) {
                updateAction = "updated";
                datasource.updatePlant(workingPlant);
            }
            else {
                updateAction = "deleted";
                datasource.deletePlant(workingPlant.getPlantID());
            }
        }
            Log.i(LOGTAG, "---------------------");
            Log.i(LOGTAG, "Now updating plant record");
            Log.i(LOGTAG, "---------------------");
            Log.i(LOGTAG, "Plant " + updateAction + " with id " + workingPlant.getPlantID());
            Log.i(LOGTAG, "The plant type is " + plantType);
            Log.i(LOGTAG, "The plant schedule is " + plantSched);
            Log.i(LOGTAG, "The last Watered date is " + workingPlant.getPlantLastWatered());
            Log.i(LOGTAG, "The picture path is " + workingPlant.getPlantImage());
            Log.i(LOGTAG, "---------------------");

        datasource.close();

        // TOAST ---------------------------------------
        Context context = getApplicationContext();
        CharSequence text = "Your plant has been " + updateAction;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        // END TOAST------------------------------------
        // TODO: Add a custom toast layout
        finish();
    }

//----------------------
// TAKE PHOTO
//----------------------
    static final int REQUEST_IMAGE_CAPTURE = 509;
    //TODO create a resize photo/trim option

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
            filename=saveToInternalStorage(imageBitmap);
            cameraShot.setImageURI(Uri.parse(filename));
            photoTaken = true;
            workingPlant.setPlantImage(filename);
        }}

    private String saveToInternalStorage(Bitmap bitmapImage){
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


    //-------------------------
    // DATE PICKER FRAGMENT
    //-------------------------
    public static class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

        public static String sqlFormattedDate;
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
            Log.i(LOGTAG, "Year: " + year +  " Month: " + month + " Day: " + day);
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
