package cz.pikadorama.uome.common.util;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import cz.pikadorama.uome.R;

import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class SnackbarHelper {

    private final Activity activity;
    private final View contentView;

    public SnackbarHelper(Activity activity) {
        this.activity = activity;
        this.contentView = activity.findViewById(android.R.id.content);
    }

    public void info(int stringId) {
        Snackbar.make(contentView, stringId, LENGTH_SHORT).show();
    }

    public void warn(int stringId) {
        show(stringId, R.color.snackbar_warn, LENGTH_SHORT);
    }

    public void error(int stringId) {
        show(stringId, R.color.snackbar_error, LENGTH_LONG);
    }

    private void show(int stringId, int colorId, int length) {
        Snackbar snackbar = Snackbar.make(contentView, stringId, length);

        TextView text = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(activity.getResources().getColor(colorId));

        snackbar.show();
    }

}
