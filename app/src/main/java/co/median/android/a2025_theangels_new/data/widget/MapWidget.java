// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.widget;
// IMPORTS
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import co.median.android.a2025_theangels_new.R;
import co.median.android.a2025_theangels_new.ui.events.create.NewEventActivity;
import co.median.android.a2025_theangels_new.ui.home.HomeActivity;
// MapWidget - Widget that displays a map preview
public class MapWidget extends AppWidgetProvider {
    @Override
// Performs on update.
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_map);
            Intent mapIntent = new Intent(context, HomeActivity.class);
            PendingIntent pendingMap = PendingIntent.getActivity(context, 0, mapIntent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.map_preview, pendingMap);
            Intent eventIntent = new Intent(context, NewEventActivity.class);
            PendingIntent pendingEvent = PendingIntent.getActivity(context, 1, eventIntent, PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.btn_new_event, pendingEvent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
