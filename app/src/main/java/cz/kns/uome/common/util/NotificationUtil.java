package cz.kns.uome.common.util;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import cz.kns.uome.common.Constants;
import cz.kns.uome.service.NotificationScheduleReceiver;

public class NotificationUtil {

	private NotificationUtil() {}

	public static void registerReceiver(Context context, int debtAge) {
		Intent intent = new Intent(context, NotificationScheduleReceiver.class);
		intent.putExtra(Constants.PREF_DEBT_AGE, debtAge);
		updateAlarmManager(context, intent);
	}

	public static void unregisterReceiver(Context context) {
		Intent intent = new Intent(context, NotificationScheduleReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}

	private static void updateAlarmManager(Context context, Intent intent) {
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 10);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, pendingIntent);
	}
}
