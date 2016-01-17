package cz.pikadorama.uome.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.util.Intents;
import cz.pikadorama.uome.common.util.NotificationUtil;

public class StartupActivity extends Activity {

    private static final String VERSION_CODE = "versionCode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initNotifications();
        openLastOpenedGroup();
    }

    /**
     * Init notifications after an update
     */
    private void initNotifications() {
        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            boolean allowNotifications = preferences.getBoolean(Constants.PREF_ALLOW_NOTIFICATIONS, false);
            if (allowNotifications) {
                int currentVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                int storedVersionCode = preferences.getInt(VERSION_CODE, 0);
                if (storedVersionCode != 0 && currentVersionCode > storedVersionCode) {
                    int debtAge = preferences.getInt(Constants.PREF_DEBT_AGE, 0);
                    if (debtAge != 0) {
                        NotificationUtil.registerReceiver(this, debtAge);
                    }
                }
                saveNewVersionCode(currentVersionCode, preferences);
            }
        } catch (NameNotFoundException e) {
            Log.e(VERSION_CODE, "Cannot read versionCode.");
        } catch (Exception e) {
            Log.e(VERSION_CODE, "Cannot update notifications. " + e.getMessage(), e);
        }
    }

    private void saveNewVersionCode(int currentVersionCode, SharedPreferences preferences) {
        Editor editor = preferences.edit();
        editor.putInt(VERSION_CODE, currentVersionCode);
        editor.commit();
    }

    private void openLastOpenedGroup() {
        long groupId = PreferenceManager.getDefaultSharedPreferences(this)
                .getLong(Constants.PREF_LAST_OPENED_GROUP, Constants.SIMPLE_GROUP_ID);
        Intent intent = Intents.openGroup(this, groupId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
