package heering.helloaloe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import eu.janmuller.android.simplecropimage.CropImage;
import heering.helloaloe.Database.PlantDataSource;
import heering.helloaloe.JsonReader.JsonPlantReader;
import heering.helloaloe.WaterNotification.AlarmMangerHelper;

/**
 * Created by Matthew on 11/19/2014.
 *
 * This section allows a user to add a plant
 *
 */
public class PlantDetails extends Activity implements View.OnClickListener, AlarmMangerHelper {

    public static final String LOGTAG = "USERPLANTS";
    public EditText plantNickNameTXTMSG;
    public TextView displayDPfDate;
    public PlantDataSource datasource;
    public Spinner spinner;
    public long plantID;
    Plant knownPlant = new Plant();
    Plant workingPlant = new Plant();
    public String mCurrentPhotoPath;
    public String plantType;
    public boolean photoTaken;
    public boolean newPlant;
    public TextView deletePlant;
    public TextView saveData;
    public ImageView cameraShot;
    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public DatePickerFragment datePicker;
    String plantWateredDate;
    public Boolean updatePlant;
    public RelativeLayout windowView;
    public RelativeLayout waterView;
    public RelativeLayout soilView;
    public RelativeLayout tipsView;

  // add Alarm notification
  // http://stackoverflow.com/questions/22705776/alarm-manager-broadcast-receiver-not-stopping
  //todo: set window direction with compass (N,S,W,E) to determine light requirements


    protected void onCreate(Bundle savedInstanceState) {
       // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plant_details);

        // Create View variables
        plantNickNameTXTMSG = (EditText) findViewById(R.id.plantNickNameET);
        plantNickNameTXTMSG.setImeOptions(EditorInfo.IME_ACTION_DONE); //add done to keyboard
        spinner = (Spinner) findViewById(R.id.spinnerPlantType);
        cameraShot = (ImageView) findViewById(R.id.takePicture);
        displayDPfDate = (TextView)findViewById(R.id.displayDPdate);
        deletePlant = (TextView) findViewById(R.id.deletePlant);
        saveData = (TextView) findViewById(R.id.savePlant);
        datePicker = new DatePickerFragment(displayDPfDate);
        windowView = (RelativeLayout) findViewById(R.id.windowStats);
        waterView = (RelativeLayout) findViewById(R.id.waterStats);
        soilView = (RelativeLayout) findViewById(R.id.soilStats);
        tipsView = (RelativeLayout) findViewById(R.id.tipsWindow);
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
                Log.i(LOGTAG, "The plant schedule is " + knownPlant.getSummerSchedule());
                Log.i(LOGTAG, "The last Watered date is " + knownPlant.getPlantLastWatered());
                Log.i(LOGTAG, "The picture path is " + knownPlant.getPlantImage());
                Log.i(LOGTAG, "---------------------");

            plantWateredDate = knownPlant.getPlantLastWatered();
            int monthParsed = Integer.parseInt(plantWateredDate.substring(5,7));
            int dayParsed = Integer.parseInt(plantWateredDate.substring(8));
            displayDPfDate.setText(MONTHS[monthParsed - 1] + " " + dayParsed);
            Log.i(LOGTAG, "displayDPDate is: " +  MONTHS[monthParsed - 1] + " " + dayParsed);
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
                    int summerSched = plantSelection.getSummerSchedule();
                    int winterSched = plantSelection.getWinterSchedule();
                    workingPlant.setSummerSchedule(summerSched);
                    workingPlant.setWinterSchedule(winterSched);
                    String soilType = plantSelection.getSoilType();
                    workingPlant.setSoilType(soilType);
                    String lightType = plantSelection.getLightType();
                    workingPlant.setLight(lightType);
                    String waterDesc= plantSelection.getWaterDes();
                    workingPlant.setWaterDes(waterDesc);

                    if (!plantType.equals("Select Plant Type")) {
                        saveData.setVisibility(View.VISIBLE);
                        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                            setTips();
                    }
                    if (!photoTaken) {
                        String fileDrawable = plantSelection.getPlantImage();
                        String filename = "android.resource://heering.helloaloe/drawable/" + fileDrawable;
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
                    saveData.setVisibility(View.VISIBLE);

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


    private void setTips(){
        waterView.setVisibility(View.VISIBLE);
        soilView.setVisibility(View.VISIBLE);
        windowView.setVisibility(View.VISIBLE);
        tipsView.setVisibility(View.VISIBLE);

        // Set views
        TextView windowTit = (TextView) findViewById(R.id.lightTitle);
        TextView window1Txt = (TextView) findViewById(R.id.window1);
        TextView window1Dsc = (TextView) findViewById(R.id.dimens1);
        TextView window2Txt = (TextView) findViewById(R.id.window2);
        TextView window2Dsc = (TextView) findViewById(R.id.dimens2);
        TextView window3Txt = (TextView) findViewById(R.id.window3);
        TextView window3Dsc = (TextView) findViewById(R.id.dimens3);
        TextView waterDscTxt = (TextView) findViewById(R.id.waterTxt);
        TextView soilTxt = (TextView) findViewById(R.id.soilDesc);

        waterDscTxt.setText(workingPlant.getWaterDes());
        soilTxt.setText(workingPlant.getSoilType());
        //set windows
        String lightType = workingPlant.getLightType();
        if (lightType.equals("low")){
            windowTit.setText("Low Light");
            window1Txt.setText("North");
            window1Dsc.setText("1 - 3 ft");
            window2Txt.setText("East/West");
            window2Dsc.setText("2 - 10 ft");
            window3Txt.setText("South");
            window3Dsc.setText("15 - 20 ft");
        }
        else if (lightType.equals("medium")){
            windowTit.setText("Medium Light");
            window1Txt.setText("North");
            window1Dsc.setText("0 ft");
            window2Txt.setText("East/West");
            window2Dsc.setText("1 - 3 ft");
            window3Txt.setText("South");
            window3Dsc.setText("3 - 10 ft");
        }
        else {
                windowTit.setText("High Light");
                window1Txt.setText("East/West");
                window1Dsc.setText("0 ft");
                window2Txt.setText("South");
                window2Dsc.setText("2 - 5 ft");
        }
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
            saveData.setVisibility(View.VISIBLE);
        }
        if (selectedView.getId() == R.id.savePlant) {
            Boolean updatePlant = true;
            saveUpdateDeletePlant(updatePlant);
        }

        if (selectedView.getId() == R.id.deletePlant) {
            //
            AlertDFragment alertdFragment = new AlertDFragment();
            // Show Alert DialogFragment
            alertdFragment.show(getFragmentManager(), "Alert Dialog Fragment");

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
            //set alarm with context,plant object, true to set an alarm vs. cancel
            SetAlarm.setAlarmManager(this,workingPlant.getPlantID(),true);
        }
        else {
            if (updatePlant) {
                updateAction = "updated";
                datasource.updatePlant(workingPlant);
                if (!plantWateredDate.equals(workingPlant.getPlantLastWatered())){
                   //set alarm with context,plant object, set new alarm (overwrites existing)
                    SetAlarm.setAlarmManager(this,workingPlant.getPlantID(),true);
                }}
            else {
                updateAction = "deleted";
                // cancel existing alarm
                SetAlarm.setAlarmManager(this,workingPlant.getPlantID(),false);
                datasource.deletePlant(workingPlant.getPlantID());
            }
        }
            Log.i(LOGTAG, "---------------------");
            Log.i(LOGTAG, "Now updating plant record");
            Log.i(LOGTAG, "---------------------");
            Log.i(LOGTAG, "Plant " + updateAction + " with id " + workingPlant.getPlantID());
            Log.i(LOGTAG, "The plant type is " + plantType);
            Log.i(LOGTAG, "The plant summer schedule is " + workingPlant.getSummerSchedule());
            Log.i(LOGTAG, "The plant winter schedule is " + workingPlant.getWinterSchedule());
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

    final int REQUEST_CODE_CROP_IMAGE = 212;
    static final int REQUEST_IMAGE_CAPTURE = 509;

    private void runCropImage() {

        // create explicit intent
        Intent intent = new Intent(this, CropImage.class);

        // tell CropImage activity to look for image to crop
        intent.putExtra(CropImage.IMAGE_PATH, mCurrentPhotoPath);

        // allow CropImage activity to rescale image
        intent.putExtra(CropImage.SCALE, true);

        // if the aspect ratio is fixed to ratio 3/2
        intent.putExtra(CropImage.ASPECT_X, 2);
        intent.putExtra(CropImage.ASPECT_Y, 2);

        // start activity CropImage with certain request code and listen
        // for result
        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                runCropImage();
                photoTaken = true;
                setPic();
                Log.i(LOGTAG, "The image path is: " + mCurrentPhotoPath);
                workingPlant.setPlantImage(mCurrentPhotoPath);
                break;

            case REQUEST_CODE_CROP_IMAGE:
                String path = data.getStringExtra(CropImage.IMAGE_PATH);
                // if nothing received
                if (path == null) {
                   return;
                }
                // cropped bitmap

               Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
               bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false);

                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(mCurrentPhotoPath);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, out); // bmp is your Bitmap instance

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void setPic() {
        // Get the dimensions of the View
        int targetW = cameraShot.getWidth();
        int targetH = cameraShot.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        cameraShot.setImageBitmap(bitmap);
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
             }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
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


    public class AlertDFragment extends DialogFragment {
        //http://www.androidbegin.com/tutorial/android-dialogfragment-tutorial/
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    // Set Dialog Icon
                    .setIcon(R.drawable.plantdelete)
                            // Set Dialog Title
                    .setTitle("delete?")
                            // Set Dialog Message
                    //.setMessage("Alert DialogFragment Tutorial")

                            // Positive button
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Boolean updatePlant = false;
                            saveUpdateDeletePlant(updatePlant);
                        }
                    })

                            // Negative Button
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,	int which) {
                            // Do something else
                        }
                    }).create();
        }
    }


}