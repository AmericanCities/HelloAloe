package heering.helloaloe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Matthew on 11/19/2014.
 */
public class AddPlant extends Activity implements View.OnClickListener{
    public static final String LOGTAG ="ALOE_LOG";
    public EditText textmsg;
    public ImageView plantPic;

    static final int READ_BLOCK_SIZE = 100;
    String FILENAME = "user_plants.txt";


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_plant);

        // hide keyboard on startup
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        textmsg = (EditText)(findViewById(R.id.plantType));
        Button saveData = (Button)findViewById(R.id.savePlant);

        File f = getFilesDir();
        String path = f.getAbsolutePath();
        Log.i(LOGTAG,"internal file path " + path);

        if (saveData != null)
            saveData.setOnClickListener(this);

        Button takePicture = (Button)findViewById(R.id.takePicture);
        if (takePicture != null)
            takePicture.setOnClickListener(this);
    }




    @Override
    public void onClick(View selectedView) {
        if (selectedView.getId()== R.id.takePicture) {
       //     String stringInput = textmsg.getText().toString();
      //      try {
       //         FileOutputStream fos = openFileOutput(FILENAME,MODE_PRIVATE);
       //         fos.write(stringInput.getBytes());
      //          fos.close();
                //display file saved message
      //          Toast.makeText(getBaseContext(), "File saved successfully!",
      //                  Toast.LENGTH_SHORT).show();

      //      } catch (Exception e) {
       //         Toast.makeText(getBaseContext(), "File failed to save",
      //                  Toast.LENGTH_SHORT).show();
      //          e.printStackTrace();
      //      }

            dispatchTakePictureIntent();
            // Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            // startActivityForResult(intent,0);
        }
    }

  //  @Override
  //  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  //      super.onActivityResult(requestCode, resultCode, data);
  //      Bitmap bp = (Bitmap) data.getExtras().get("data");
  //      plantPic.setImageBitmap(bp);
  //  }


    // http://developer.android.com/training/camera/photobasics.html
    String mCurrentPhotoPath;

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
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
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
            } catch (IOException e) {
                // Error occurred while creating the File
                e.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    //http://stackoverflow.com/questions/9273008/android-save-images-to-sqlite-or-sdcard-or-memory/9273045#9273045
   // long selectedImageUri = ContentUris.parseId(Uri.parse(anniEntry.getUri()));
   // Bitmap bm = MediaStore.Images.Thumbnails.getThumbnail(
   //         mContext.getContentResolver(), selectedImageUri,MediaStore.Images.Thumbnails.MICRO_KIND,
   //         null );

 }

