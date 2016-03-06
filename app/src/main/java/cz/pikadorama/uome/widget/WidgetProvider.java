package cz.pikadorama.uome.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.activity.SimplePersonDetailActivity;
import cz.pikadorama.uome.activity.StartupActivity;
import cz.pikadorama.uome.common.util.BalanceCategory;
import cz.pikadorama.uome.widget.config.WidgetPreferences;

public class WidgetProvider extends AppWidgetProvider {

    public static void updateAllWidgets(Context context) {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int widgetIds[] = widgetManager.getAppWidgetIds(new ComponentName(context, WidgetProvider.class));
        for (int widgetId : widgetIds) {
            updateWidget(context, widgetManager, widgetId);
            widgetManager.notifyAppWidgetViewDataChanged(widgetId, android.R.id.list);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager widgetManager, int[] widgetIds) {
        for (int id : widgetIds) {
            updateWidget(context, widgetManager, id);
        }
        super.onUpdate(context, widgetManager, widgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] widgetIds) {
        WidgetPreferences prefs = new WidgetPreferences(context);
        for (int widgetId : widgetIds) {
            prefs.getWidgetPreferences(widgetId).edit().clear().commit();
        }
        super.onDeleted(context, widgetIds);
    }

    private static void updateWidget(Context context, AppWidgetManager widgetManager, int widgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        // set list adapter
        views.setRemoteAdapter(android.R.id.list, createRemoteIntent(context, widgetId, WidgetService.class));
        views.setEmptyView(android.R.id.list, android.R.id.empty);

        // set widget title
        BalanceCategory category = new WidgetPreferences(context).getSavedCategory(widgetId);
        views.setTextViewText(R.id.titleText, context.getString(category.widgetTitleResId()));

        // set onclick listener - we create a pending intent template and when an items is clicked
        // the intent is filled with missing data and sent
        views.setPendingIntentTemplate(android.R.id.list,
                createRemotePendingIntent(context,
                        widgetId,
                        SimplePersonDetailActivity.class,
                        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

        // onclick listener for the header - run home screen
        views.setOnClickPendingIntent(R.id.titleText,
                createRemotePendingIntent(context,
                        widgetId,
                        StartupActivity.class,
                        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

        widgetManager.updateAppWidget(widgetId, views);
    }

    private static Intent createRemoteIntent(Context context, int widgetId, Class<?> activityToRun) {
        Intent intent = new Intent(context, activityToRun);
        intent.setData((ContentUris.withAppendedId(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)), widgetId)));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        return intent;
    }

    private static PendingIntent createRemotePendingIntent(
            Context context,
            int widgetId,
            Class<?> activityToRun,
            int activityIntentFlags) {
        Intent intent = createRemoteIntent(context, widgetId, activityToRun);
        intent.setFlags(activityIntentFlags);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
