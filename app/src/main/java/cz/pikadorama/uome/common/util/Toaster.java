package cz.pikadorama.uome.common.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Helper that offers more convenient work with {@link Toast}s.
 */
public class Toaster {

    private final Context context;

    /**
     * @param context the context that will be used to show the toasts
     */
    public Toaster(Context context) {
        this.context = context;
    }

    /**
     * Displays a {@link Toast} with the given message and {@link Toast#LENGTH_SHORT} duration.
     *
     * @param message a CharSequence to be displayed
     */
    public void show(CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays a {@link Toast} with the given message and {@link Toast#LENGTH_SHORT} duration.
     *
     * @param stringId resource ID of the string to be displayed
     */
    public void show(int stringId) {
        Toast.makeText(context, stringId, Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays a {@link Toast} with the given message and {@link Toast#LENGTH_LONG} duration.
     *
     * @param message a CharSequence to be displayed
     */
    public void showLong(CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Displays a {@link Toast} with the given message and {@link Toast#LENGTH_LONG} duration.
     *
     * @param stringId resource ID of the string to be displayed
     */
    public void showLong(int stringId) {
        Toast.makeText(context, stringId, Toast.LENGTH_LONG).show();
    }

}
