package heering.helloaloe;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Created by Matthew on 11/19/2014.
 */
public class AddPlant extends Activity implements View.OnClickListener{

    EditText textmsg;
    static final int READ_BLOCK_SIZE = 100;
    String FILENAME = "user_plants.txt";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_plant);
        textmsg = (EditText)(findViewById(R.id.plantType));

        Button saveData = (Button)findViewById(R.id.savePlant);
        if (saveData != null)
            saveData.setOnClickListener(this);

        Button loadData = (Button)findViewById(R.id.readPlant);
        if (loadData != null)
            loadData.setOnClickListener(this);
    }


    @Override
    public void onClick(View selectedView) {
        if (selectedView.getId()== R.id.savePlant) {
            String stringInput = textmsg.getText().toString();
            try {
                FileOutputStream fos = openFileOutput(FILENAME,MODE_PRIVATE);
                fos.write(stringInput.getBytes());
                fos.close();
                //display file saved message
                Toast.makeText(getBaseContext(), "File saved successfully!",
                        Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "File failed to save",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }


        if (selectedView.getId()== R.id.readPlant){
            try {
                FileInputStream fileIn=openFileInput(FILENAME);
                InputStreamReader InputRead= new InputStreamReader(fileIn);

                char[] inputBuffer= new char[READ_BLOCK_SIZE];
                String s="";
                int charRead;

                while ((charRead=InputRead.read(inputBuffer))>0) {
                    // char to string conversion
                    String readstring=String.copyValueOf(inputBuffer,0,charRead);
                    s +=readstring;
                }
                InputRead.close();
                Toast.makeText(getBaseContext(), s,Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
 }

