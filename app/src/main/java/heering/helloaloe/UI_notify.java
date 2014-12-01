package heering.helloaloe;

import android.app.Activity;
import android.widget.TextView;

/**
 * Created by Matthew on 11/30/2014.
 */
public class UI_notify {

    public static void displayText(Activity activity, int id, String text) {
        TextView tv = (TextView) activity.findViewById(id);
        tv.setText(text);
    }

}
