package heering.helloaloe.Widgetry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import heering.helloaloe.PlantDataSource;
import heering.helloaloe.R;

/**
 * Created by Matt on 12/14/2014.
 */
public class RemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {


    private static final String LOGTAG = "USERPLANTS";
    PlantDataSource datasource;
    public static ArrayList plantImageItems;



    private static final String[] items={"loem", "im", "dor",
            "sit", "amet", "coer",
            "adng", "elit", "mobi",
            "vel", "liga", "vitae",
            "arcu", "aliet", "molis",
            "etam", "vel", "erat",
            "plrat", "ante",
            "porr", "soes",
            "peue", "aue",
            "puus"};



    //private CallsDataSrouce mDataSource;
    //private List<CallItem> mCallsList;

    private Context mContext = null;

    public RemoteViewFactory(Context context, Intent intent) {
        this.mContext = context;



    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews row=new RemoteViews("heering.helloaloe",
                heering.helloaloe.R.layout.widget_listview_item);

        row.setTextViewText(heering.helloaloe.R.id.textW,items[position]);


        //Intent i=new Intent();
        //Bundle extras=new Bundle();

       // extras.putString(WidgetProvider.EXTRA_WORD, items[position]);
       // i.putExtras(extras);
       //   row.setOnClickFillInIntent(android.R.id.text1, i);

        return(row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
     //   mDataSource = new CallsDataSrouce(mContext);
     //   mDataSource.open();
     //    mCallsList = mDataSource.findAllCalls();
    }

    @Override
    public void onDataSetChanged() {
     //   mCallsList = mDataSource.findAllCalls();
    }

    @Override
    public void onDestroy() {
    }
}