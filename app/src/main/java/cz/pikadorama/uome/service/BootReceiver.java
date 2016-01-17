package cz.pikadorama.uome.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.util.NotificationUtil;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        final boolean allowNotifications = preferences.getBoolean(
                Constants.PREF_ALLOW_NOTIFICATIONS, false);
        Log.d(TAG, "AllowNotifications: " + allowNotifications);
        if (allowNotifications) {
            final int debtAge = Integer.parseInt(preferences.getString(Constants.PREF_DEBT_AGE,
                    Constants.DEFAULT_PREF_DEBT_AGE));
            Log.d(TAG, "DebtAge: " + debtAge);
            NotificationUtil.registerReceiver(context, debtAge);
        }
    }
}