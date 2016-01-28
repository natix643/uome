package cz.pikadorama.uome.common.util;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import cz.pikadorama.uome.R;

import static android.support.design.widget.Snackbar.*;

public class SnackbarHelper {

    private final Activity activity;
    private final View contentView;

    public SnackbarHelper(Activity activity) {
        this.activity = activity;
        this.contentView = activity.findViewById(android.R.id.content);
    }

    public void info(int stringId) {
        Snackbar snackbar = Snackbar.make(contentView, stringId, LENGTH_SHORT);
        dismissOnClick(snackbar);
        snackbar.show();
    }

    public void warn(int stringId) {
        show(stringId, R.color.snackbar_warn, LENGTH_SHORT);
    }

    public void error(int stringId) {
        show(stringId, R.color.snackbar_error, LENGTH_LONG);
    }

    private void show(int stringId, int colorId, int length) {
        Snackbar snackbar = Snackbar.make(contentView, stringId, length);
        dismissOnClick(snackbar);

        TextView text = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(activity.getResources().getColor(colorId));

        snackbar.show();
    }

    private void dismissOnClick(final Snackbar snackbar) {
        snackbar.getView().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
    }

}
