package heering.helloaloe;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
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


//@TODO update refresh list view after creating a new plant
//@TODO update homescreen layout for Relative percentage view

public class HomeScreen extends Activity implements View.OnClickListener {

    private static final String LOGTAG = "USERPLANTS";
    PlantDataSource datasource;
    public static ArrayList plantItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOGTAG,"And we are off and running");

        setContentView(R.layout.home_screen);

        Button addPlant = (Button)findViewById(R.id.addPlantbtn);
        if (addPlant != null)
            addPlant.setOnClickListener(this);

        datasource = new PlantDataSource(this);
        datasource.open();
        plantItems = datasource.getListItems();
        datasource.close();
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container3, new PlantListFragment())
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
     * A placeholder fragment containing a simple view.
     */
    public static class PlantListFragment extends ListFragment {

        //@Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // initialize and set the list adapter
            Log.i(LOGTAG,"There are " + plantItems.size() + " items");
            setListAdapter(new ListViewPlantAdapter(getActivity(), plantItems));
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