package heering.helloaloe.Widgetry;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Matt on 12/14/2014.
 */
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        RemoteViewsFactory listProvider = new RemoteViewFactory(this.getApplicationContext(), intent);
        return listProvider;
    }}
