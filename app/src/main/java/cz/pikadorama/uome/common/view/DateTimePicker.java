package cz.pikadorama.uome.common.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.format.DateTimeFormatter;
import cz.pikadorama.uome.common.fragment.BaseDialogFragment;
import cz.pikadorama.uome.common.util.Views;

import static java.util.Calendar.*;

public class DateTimePicker extends LinearLayout {

    private static final String MILLIS = "millis";

    private final Activity activity;

    private final DateTimeFormatter dateFormatter;
    private final DateTimeFormatter timeFormatter;

    private final Calendar dateTime = Calendar.getInstance();

    private TextView datePicker;
    private TextView timePicker;

    public DateTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.activity = (Activity) context;
        this.dateFormatter = DateTimeFormatter.showDate(context).showWeekDay();
        this.timeFormatter = DateTimeFormatter.showTime(context);

        LayoutInflater.from(context).inflate(R.layout.date_time_picker, this);

        initPickers();
    }

    private void initPickers() {
        datePicker = Views.require(this, R.id.datePicker);
        datePicker.setOnClickListener(onDateClickListener);

        timePicker = Views.require(this, R.id.timePicker);
        timePicker.setOnClickListener(onTimeClickListener);

        refreshPickers();
    }

    public long getMillis() {
        return dateTime.getTimeInMillis();
    }

    public void setMillis(long millis) {
        dateTime.setTimeInMillis(millis);
        refreshPickers();
    }

    public Date getDateTime() {
        return dateTime.getTime();
    }

    public void setDateTime(Date dateTime) {
        this.dateTime.setTime(dateTime);
        refreshPickers();
    }

    private void refreshPickers() {
        refreshDatePicker();
        refreshTimePicker();
    }

    private void refreshDatePicker() {
        datePicker.setText(dateFormatter.format(dateTime));
    }

    private void refreshTimePicker() {
        timePicker.setText(timeFormatter.format(dateTime));
    }

    private final OnClickListener onDateClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            new DateDialog().show(activity);
        }
    };

    private final OnClickListener onTimeClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            new TimeDialog().show(activity);
        }
    };

    @Override
    protected Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();

        Bundle bundle = new Bundle();
        bundle.putLong(MILLIS, getMillis());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(null);

        Bundle bundle = (Bundle) state;
        setMillis(bundle.getLong(MILLIS));
    }

    public interface Holder {
        DateTimePicker getDateTimePicker();
    }

    public static class DateDialog extends BaseDialogFragment {

        private Holder holder;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            holder = (Holder) activity;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final DateTimePicker picker = holder.getDateTimePicker();
            final Calendar dateTime = picker.dateTime;

            return new DatePickerDialog(
                    getActivity(),
                    new OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            dateTime.set(YEAR, year);
                            dateTime.set(MONTH, monthOfYear);
                            dateTime.set(DAY_OF_MONTH, dayOfMonth);
                            picker.refreshDatePicker();
                        }
                    },
                    dateTime.get(YEAR),
                    dateTime.get(MONTH),
                    dateTime.get(DAY_OF_MONTH));
        }
    }

    public static class TimeDialog extends BaseDialogFragment {

        private Holder holder;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            holder = (Holder) activity;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final DateTimePicker picker = holder.getDateTimePicker();
            final Calendar dateTime = picker.dateTime;

            return new TimePickerDialog(
                    getActivity(),
                    new OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            dateTime.set(HOUR_OF_DAY, hourOfDay);
                            dateTime.set(MINUTE, minute);
                            picker.refreshTimePicker();
                        }
                    },
                    dateTime.get(HOUR_OF_DAY),
                    dateTime.get(MINUTE),
                    DateFormat.is24HourFormat(getActivity()));
        }
    }

}