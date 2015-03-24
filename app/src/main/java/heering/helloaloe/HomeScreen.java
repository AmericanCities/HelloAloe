package heering.helloaloe;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import heering.helloaloe.Database.PlantDataSource;



//@TODO update homescreen layout for Relative percentage view
//@TODO delete pendingIntent variable

public class HomeScreen extends Activity implements View.OnClickListener {

    private static final String LOGTAG = "USERPLANTS";
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String SETSUMMER = "setSummer";

    // private PendingIntent pendingIntent; // for testing notifications

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean previouslyStarted= prefs.getBoolean(SETSUMMER,false);
        if(!previouslyStarted){
            startActivity(new Intent(HomeScreen.this,FirstLaunch.class));
        }
        Log.i(LOGTAG,"And we are off and running");

        setContentView(R.layout.home_screen);
        Button addPlant = (Button)findViewById(R.id.addPlantbtn);
        if (addPlant != null)
            addPlant.setOnClickListener(this);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.plantListView, new PlantListFragment())
                    .commit();
        }
    }
    public void onClick(View selectedView){
        if (selectedView.getId()== R.id.addPlantbtn){
            Intent addPlantIntent = new Intent(HomeScreen.this,PlantDetails.class);
            boolean newPlant = true;
            addPlantIntent.putExtra("newPlant", newPlant);
            startActivity(addPlantIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Fragment containing a listView
     */
   public static class PlantListFragment extends ListFragment {
          private PlantDataSource dbHelper;
          private ListViewPlantAdapter adapter;
          private ArrayList plantItems;

        //@Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            dbHelper = new PlantDataSource(getActivity());
            plantItems = dbHelper.getListItems();
            adapter = new ListViewPlantAdapter(getActivity(), plantItems);

            // initialize and set the list adapter
            setListAdapter(adapter);
        }

       public void onResume(){
              super.onResume();
              Log.i(LOGTAG,"And we are Resuming");
              plantItems.clear();
              plantItems.addAll(dbHelper.getListItems());
              adapter.notifyDataSetChanged();
              //http://stackoverflow.com/questions/14503006/android-listview-not-refreshing-after-notifydatasetchanged
     }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            // remove the dividers from the ListView of the ListFragment
            getListView().setDivider(null);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            // retrieve theListView item
            ListViewItem item = (ListViewItem) plantItems.get(position);

            // do something
            Toast.makeText(getActivity(), item.plantNickName, Toast.LENGTH_SHORT).show();
            Intent editPlantIntent = new Intent(getActivity(),PlantDetails.class);
            Long plantID = item.plantID;
            boolean newPlant = false;
            editPlantIntent.putExtra("newPlant", newPlant);
            editPlantIntent.putExtra("plantID",plantID);
            startActivity(editPlantIntent);
        }
    }
}