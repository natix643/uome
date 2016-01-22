package cz.pikadorama.uome.common.util;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import cz.pikadorama.uome.R;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class SnackbarHelper {

    private final Activity activity;
    private final View contentView;

    public SnackbarHelper(Activity activity) {
        this.activity = activity;
        this.contentView = activity.findViewById(android.R.id.content);
    }

    public void info(View view, int stringId) {
        Snackbar.make(view, stringId, LENGTH_SHORT).show();
    }

    public void info(int stringId) {
        info(contentView, stringId);
    }

    public void warn(View view, int stringId) {
        Snackbar snackbar = Snackbar.make(view, stringId, LENGTH_SHORT);

        TextView text = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(activity.getResources().getColor(R.color.snackbar_warn));

        snackbar.show();
    }

    public void warn(int stringId) {
        warn(contentView, stringId);
    }

}
