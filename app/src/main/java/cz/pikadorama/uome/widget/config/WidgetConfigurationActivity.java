package cz.pikadorama.uome.widget.config;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.activity.UomeActivity;
import cz.pikadorama.uome.common.util.BalanceCategory;
import cz.pikadorama.uome.common.view.SnackbarHelper;
import cz.pikadorama.uome.widget.WidgetProvider;

public class WidgetConfigurationActivity extends UomeActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.widget_configuration;
    }

    public static class ConfigurationFragment extends PreferenceFragment {

        private static final String WIDGET_PREF_DATA_CATEGORY = "dataCategory";
        private static final String WIDGET_PREF_ITEM_LIMIT = "itemLimit";

        int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

        private ListPreference debtTypePref;
        private ListPreference debtLimitPref;

        private final SharedPreferences.OnSharedPreferenceChangeListener preferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                switch (key) {
                    case WIDGET_PREF_DATA_CATEGORY:
                        debtTypePref.setSummary(debtTypePref.getEntry());
                        break;
                    case WIDGET_PREF_ITEM_LIMIT:
                        debtLimitPref.setSummary(debtLimitPref.getEntry());
                        break;
                }
            }
        };

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            widgetId = getActivity().getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                new SnackbarHelper(getActivity()).error(R.string.error_widget_not_created);
                getActivity().finish();
            }

            addPreferencesFromResource(R.xml.widget_configuration);
            debtLimitPref = (ListPreference) findPreference(WIDGET_PREF_ITEM_LIMIT);
            debtTypePref = (ListPreference) findPreference(WIDGET_PREF_DATA_CATEGORY);

            setHasOptionsMenu(true);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(preferenceListener);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(preferenceListener);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.widget_configuration, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.menu_done) {
                saveConfiguration();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void saveConfiguration() {
            WidgetPreferences prefs = new WidgetPreferences(getActivity());
            prefs.saveCategory(widgetId, BalanceCategory.valueOf(debtTypePref.getValue()));
            prefs.saveLimit(widgetId, Integer.valueOf(debtLimitPref.getValue()));

            // update the widget according to the configuration
            WidgetProvider.updateAllWidgets(getActivity());

            // clear shared preferences to have default values next time we create another widget
            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().clear().commit();

            // Make sure we pass back the original widgetId otherwise the widget
            // won't be created
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
            getActivity().setResult(RESULT_OK, resultValue);
            getActivity().finish();
        }

    }
}
