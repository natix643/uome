package cz.pikadorama.uome.widget.config;

import android.content.Context;
import android.content.SharedPreferences;

import cz.pikadorama.uome.common.util.BalanceCategory;

public class WidgetPreferences {

    private static final String PREFERENCES = "Uome.Widget.Preferences";
    private static final String PREFERENCES_KEY_ITEM_LIMIT = "Uome.Widget.Item.Limit";
    private static final String PREFERENCES_KEY_DATA_CATEGORY = "Uome.Widget.Data.Category";

    private final Context context;

    public WidgetPreferences(Context context) {
        this.context = context;
    }

    public String getPreferencesKey(int widgetId) {
        return PREFERENCES + String.valueOf(widgetId);
    }

    public SharedPreferences getWidgetPreferences(int widgetId) {
        return context.getSharedPreferences(getPreferencesKey(widgetId), Context.MODE_PRIVATE);
    }

    /**
     * Get data category for the widget.
     *
     * @param widgetId widget ID
     * @return category, if not set the default is {@link BalanceCategory#OWES_ME_MOST}
     */
    public BalanceCategory getSavedCategory(int widgetId) {
        return BalanceCategory.valueOf(getWidgetPreferences(widgetId).getString(
                WidgetPreferences.PREFERENCES_KEY_DATA_CATEGORY,
                BalanceCategory.OWES_ME_MOST.name()));
    }

    /**
     * Get saved number of items to show in the widget.
     *
     * @param widgetId widget ID
     * @return limit number or -1 if not set or we want to show all
     */
    public int getSavedLimit(int widgetId) {
        return getWidgetPreferences(widgetId).getInt(PREFERENCES_KEY_ITEM_LIMIT, -1);
    }

    public void saveCategory(int widgetId, BalanceCategory category) {
        getWidgetPreferences(widgetId).edit().putString(WidgetPreferences.PREFERENCES_KEY_DATA_CATEGORY, category.name()).commit();
    }

    public void saveLimit(int widgetId, int limit) {
        getWidgetPreferences(widgetId).edit().putInt(WidgetPreferences.PREFERENCES_KEY_ITEM_LIMIT, limit).commit();
    }
}
