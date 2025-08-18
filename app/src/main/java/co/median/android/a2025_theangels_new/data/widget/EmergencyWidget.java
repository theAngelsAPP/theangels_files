// The Angels App Final Project 2025 | Omer Gamliel (209052786) & Batel Gofleyzer (211869409)
package co.median.android.a2025_theangels_new.data.widget;
// IMPORTS
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;
import co.median.android.a2025_theangels_new.R;
// EmergencyWidget - Widget for quick emergency actions
public class EmergencyWidget extends AppWidgetProvider {
    @Override
// Performs on update.
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_emergency);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
