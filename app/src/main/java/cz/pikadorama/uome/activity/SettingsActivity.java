package cz.pikadorama.uome.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.activity.UomeActivity;
import cz.pikadorama.uome.common.util.NotificationUtil;
import cz.pikadorama.uome.common.view.SnackbarHelper;

public class SettingsActivity extends UomeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.settings;
    }

    public static class SettingsFragment extends PreferenceFragment {

        private static final int REQUEST_BACKUP = 0;
        private static final int REQUEST_EXPORT = 1;

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

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if (Constants.PREF_BACKUP.equals(preference.getKey())) {
                if (writePermissionGranted()) {
                    startBackupActivity();
                } else {
                    requestWritePermission(REQUEST_BACKUP);
                }
                return true;
            } else if (Constants.PREF_EXPORT.equals(preference.getKey())) {
                if (writePermissionGranted()) {
                    startExportActivity();
                } else {
                    requestWritePermission(REQUEST_EXPORT);
                }
                return true;
            }

            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        private boolean writePermissionGranted() {
            return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }

        private void requestWritePermission(int requestCode) {
            FragmentCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        }

        private void startExportActivity() {
            startActivity(new Intent(getActivity(), ExportActivity.class));
        }

        private void startBackupActivity() {
            startActivity(new Intent(getActivity(), BackupActivity.class));
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                switch(requestCode) {
                    case REQUEST_BACKUP:
                        startBackupActivity();
                        break;
                    case REQUEST_EXPORT:
                        startExportActivity();
                        break;
                }
            } else {
                new SnackbarHelper(getActivity()).warn(R.string.permission_not_granted);
            }
        }
    }
}
