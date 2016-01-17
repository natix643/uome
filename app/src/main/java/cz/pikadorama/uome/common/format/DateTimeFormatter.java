package cz.pikadorama.uome.common.format;

import android.content.Context;
import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Date;

import static android.text.format.DateUtils.*;

public class DateTimeFormatter {

    private final Context context;
    private final int flags;

    private DateTimeFormatter(Context context, int flags) {
        this.context = context;
        this.flags = flags;
    }

    public static DateTimeFormatter showTime(Context context) {
        return new DateTimeFormatter(context, FORMAT_SHOW_TIME);
    }

    public static DateTimeFormatter showDate(Context context) {
        return new DateTimeFormatter(context, FORMAT_SHOW_DATE | FORMAT_ABBREV_ALL);
    }

    public static DateTimeFormatter showDateTime(Context context) {
        return new DateTimeFormatter(context, FORMAT_SHOW_DATE | FORMAT_ABBREV_ALL | FORMAT_SHOW_TIME);
    }

    public DateTimeFormatter showWeekDay() {
        return new DateTimeFormatter(context, flags | FORMAT_SHOW_WEEKDAY);
    }

    public DateTimeFormatter showYearAlways() {
        return new DateTimeFormatter(context, flags | FORMAT_SHOW_YEAR);
    }

    public String format(Date date) {
        return DateUtils.formatDateTime(context, date.getTime(), flags);
    }

    public String format(Calendar calendar) {
        return format(calendar.getTime());
    }
}
