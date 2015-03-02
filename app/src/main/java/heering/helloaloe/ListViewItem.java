package heering.helloaloe;

/**
 * Created by Matthew on 12/1/2014.
 */
public class ListViewItem {
    public final String plantImage;         // the drawable for the ListView item ImageView
    public final String plantType;          // the text for the ListView item title
    public final String plantNickName;      // the text for the ListView item description
    public final Long plantID;

    public ListViewItem(String plantType, String plantNickName, String plantImage, long plantID) {

        this.plantImage = plantImage;
        this.plantType = plantType;
        this.plantNickName = plantNickName;
        this.plantID = plantID;
    }
}