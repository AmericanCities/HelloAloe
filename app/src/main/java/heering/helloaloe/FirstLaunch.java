package heering.helloaloe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Matt on 3/23/2015.
 */
public class FirstLaunch extends Activity implements View.OnClickListener  {

    private static final String LOGTAG = "USERPLANTS";
    public SharedPreferences prefs;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String SETSUMMER = "setSummer";
    public static final String ALLSSUMMER = "endlessSummer";
    public ImageView summerSelector, winterSelector;
    public TextView continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOGTAG, "We have a first Launcher");
        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        setContentView(R.layout.season_select);
        summerSelector = (ImageView)findViewById(R.id.summerSelector);
        if (summerSelector != null)
            summerSelector.setOnClickListener(this);

        winterSelector = (ImageView)findViewById(R.id.winterSelector);
        if (winterSelector != null)
            winterSelector.setOnClickListener(this);

        continueButton =(TextView)findViewById(R.id.continueButton);
        if (continueButton != null)
            continueButton.setOnClickListener(this);
    }
    public void onClick(View selectedView){
        if (selectedView.getId()== R.id.summerSelector){
           summerSelector.setImageResource(R.drawable.summerselected);
           // just in case winter is already selected
           winterSelector.setImageResource(R.drawable.winter);
           SharedPreferences.Editor edit = prefs.edit();
           edit.putBoolean(ALLSSUMMER, Boolean.TRUE);
           edit.commit();
           continueButton.setVisibility(View.VISIBLE);
        }
        if (selectedView.getId()== R.id.winterSelector){
            winterSelector.setImageResource(R.drawable.winterselected);
            // just in case winter is already selected
            summerSelector.setImageResource(R.drawable.summer);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(ALLSSUMMER, Boolean.FALSE);
            continueButton.setVisibility(View.VISIBLE);
            edit.commit();
        }

        if (selectedView.getId()== R.id.continueButton){
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean(SETSUMMER, Boolean.TRUE);
            edit.commit();
            startActivity(new Intent(FirstLaunch.this,HomeScreen.class));
        }
    }
}
