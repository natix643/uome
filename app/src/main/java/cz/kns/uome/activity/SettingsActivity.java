package cz.kns.uome.activity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

import cz.kns.uome.R;
import cz.kns.uome.common.Constants;
import cz.kns.uome.common.activity.UomeActivity;
import cz.kns.uome.common.util.NotificationUtil;

public class SettingsActivity extends UomeActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction()
				.replace(R.id.container, new SettingsFragment())
				.commit();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.settings;
	}

	public static class SettingsFragment extends PreferenceFragment {

		private SharedPreferences preferences;

		private SwitchPreference allowNotificationsPref;
		private ListPreference debtAgePref;

		private final OnSharedPreferenceChangeListener preferenceListener = new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
				if (key.equals(Constants.PREF_ALLOW_NOTIFICATIONS) || key.equals(Constants.PREF_DEBT_AGE)) {
					if (allowNotificationsPref.isChecked()) {
						NotificationUtil.registerReceiver(getActivity(), Integer.parseInt(debtAgePref.getValue()));
					} else {
						NotificationUtil.unregisterReceiver(getActivity());
					}
				}
			}
		};

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			addPreferencesFromResource(R.xml.preferences);

			preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

			allowNotificationsPref = (SwitchPreference) findPreference(Constants.PREF_ALLOW_NOTIFICATIONS);
			debtAgePref = (ListPreference) findPreference(Constants.PREF_DEBT_AGE);
		}

		@Override
		public void onResume() {
			super.onResume();
			preferences.registerOnSharedPreferenceChangeListener(preferenceListener);
		}

		@Override
		public void onPause() {
			super.onPause();
			preferences.unregisterOnSharedPreferenceChangeListener(preferenceListener);
		}
	}

}
