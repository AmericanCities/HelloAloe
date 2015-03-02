package heering.helloaloe;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.net.URI;
import java.util.List;

/**
 * Created by Matthew on 12/1/2014.
 *
 * Sourced from : Sourced from :: http://www.perfectapk.com/android-listfragment-tutorial.html
 **/


public class ListViewPlantAdapter extends ArrayAdapter<ListViewItem> {

    public ListViewPlantAdapter(Context context, List<ListViewItem> items) {
        super(context, R.layout.listview_item, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listview_item, parent, false);

            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.plantImage = (ImageView) convertView.findViewById(R.id.plantImage);
            viewHolder.plantType = (TextView) convertView.findViewById(R.id.plantType);
            viewHolder.plantNickName = (TextView) convertView.findViewById(R.id.plantNickName);
            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        ListViewItem item = getItem(position);
        viewHolder.plantType.setText(item.plantType);
        viewHolder.plantNickName.setText(item.plantNickName);
        viewHolder.plantImage.setImageURI(Uri.parse(item.plantImage));
        return convertView;
    }

    private static class ViewHolder {
        ImageView plantImage;
        TextView plantType;
        TextView plantNickName;
    }

    private static class UpdateList{

    }

}