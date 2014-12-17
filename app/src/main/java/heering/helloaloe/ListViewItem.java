package heering.helloaloe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;

/**
 * Created by Matthew on 12/1/2014.
 */
public class ListViewItem {
    public final String plantImage;         // the drawable for the ListView item ImageView
    public final String plantType;          // the text for the ListView item title
    public final String plantNickName;      // the text for the ListView item description

    public ListViewItem(String plantType, String plantNickName,String plantImage) {

        this.plantImage = plantImage;
        this.plantType = plantType;
        this.plantNickName = plantNickName;
    }
}
