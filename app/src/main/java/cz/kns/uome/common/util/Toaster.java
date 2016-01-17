package cz.kns.uome.common.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Helper that offers more convenient work with {@link Toast}s.
 */
public class Toaster {

    /**
     * 3x height of ActionBar in portrait
     */
    private static final int Y_OFFSET_DIPS = 144;

    private final Context context;
    private final int yOffsetPixels;

    /**
     * @param context the context that will be used to show the toasts
     */
    public Toaster(Context context) {
        this.context = context;
        this.yOffsetPixels = dipsToPixels(Y_OFFSET_DIPS);
    }

    private int dipsToPixels(int dips) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, metrics);
    }

    /**
     * Displays a {@link Toast} with the given message and {@link Toast#LENGTH_SHORT} duration.
     *
     * @param message a CharSequence to be displayed
     */
    public void show(CharSequence message) {
        show(Toast.makeText(context, message, Toast.LENGTH_SHORT));
    }

    /**
     * Displays a {@link Toast} with the given message and {@link Toast#LENGTH_SHORT} duration.
     *
     * @param stringId resource ID of the string to be displayed
     */
    public void show(int stringId) {
        show(Toast.makeText(context, stringId, Toast.LENGTH_SHORT));
    }

    /**
     * Displays a {@link Toast} with the given message and {@link Toast#LENGTH_LONG} duration.
     *
     * @param message a CharSequence to be displayed
     */
    public void showLong(CharSequence message) {
        show(Toast.makeText(context, message, Toast.LENGTH_LONG));
    }

    /**
     * Displays a {@link Toast} with the given message and {@link Toast#LENGTH_LONG} duration.
     *
     * @param stringId resource ID of the string to be displayed
     */
    public void showLong(int stringId) {
        show(Toast.makeText(context, stringId, Toast.LENGTH_LONG));
    }

    private void show(Toast toast) {
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, yOffsetPixels);
        toast.show();
    }

}
