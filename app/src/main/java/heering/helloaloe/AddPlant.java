package heering.helloaloe;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Matthew on 11/19/2014.
 */
public class AddPlant extends Activity implements View.OnClickListener {

    public static final String LOGTAG = "USERPLANTS";
    public EditText plantTypeTXTMSG;
    public EditText plantNickNameTXTMSG;
    public NumberPicker plantSchedNP = null;
    public TextView plantSchedTV;
    public TextView displayDPfDate;
    public PlantDataSource datasource;
    public String filename;
    public ImageView cameraShot;

    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_plant);
        // hide keyboard on startup
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Create View variables
        plantTypeTXTMSG = (EditText) findViewById(R.id.plantType);
        plantTypeTXTMSG.setImeOptions(EditorInfo.IME_ACTION_DONE);  //add done to keyboard
        plantNickNameTXTMSG = (EditText) findViewById(R.id.plantNickName);
        plantNickNameTXTMSG.setImeOptions(EditorInfo.IME_ACTION_DONE); //add done to keyboard
       // DatePicker plantLastWateredDP = (DatePicker)(findViewById(R.id.datePicker));

        // number picker limits
        plantSchedNP = (NumberPicker)findViewById(R.id.schdulePicker);
        plantSchedNP.setMaxValue(31);
        plantSchedNP.setMinValue(1);
        plantSchedNP.setWrapSelectorWheel(true);
        addButtonListeners();
    }


    private void addButtonListeners() {

        Button datePickerBtn = (Button)findViewById(R.id.dateButton);
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

        Button takePicture = (Button)findViewById(R.id.takePicture);
        if (takePicture != null)
            takePicture.setOnClickListener(this);

        Button saveData = (Button) findViewById(R.id.savePlant);
        if (saveData != null)
            saveData.setOnClickListener(this);

        plantSchedNP = (NumberPicker)findViewById(R.id.schdulePicker);
        plantSchedNP.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal)
            {
                plantSchedTV = (TextView) findViewById(R.id.scheduleResult);
                plantSchedTV.setText(new Integer(newVal).toString());
            }
        });
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
        String plantType = plantTypeTXTMSG.getText().toString();
        String plantNickName= plantNickNameTXTMSG.getText().toString();
        int plantSchedStr = Integer.parseInt(plantSchedTV.getText().toString());
        datasource = new PlantDataSource(this);
        datasource.open();
        Plant plant = new Plant();
        plant.setPlantType(plantType);
        plant.setPlantNickName(plantNickName);
        plant.setPlantSchedule(plantSchedStr);
        plant.setPlantLastWatered(DatePickerFragment.sqlFormattedDate);
        plant.setPlantImage(filename);
        Log.i(LOGTAG,"Now add plant record");
        plant = datasource.create(plant);
        Log.i(LOGTAG, "Plant created with id " + plant.getPlantID());
        Log.i(LOGTAG, "The plant type is " + plantType);
        Log.i(LOGTAG, "The plant nick name is " + plantNickName);
        Log.i(LOGTAG, "The plant schedule is " + plantSchedStr);
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
            ImageView cameraShot = (ImageView) findViewById(R.id.cameraShot);
            cameraShot.setImageURI(Uri.parse(filename));

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
            FileOutputStream fos = null;
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
