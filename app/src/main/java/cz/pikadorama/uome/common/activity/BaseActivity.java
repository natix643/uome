package cz.pikadorama.uome.common.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public abstract class BaseActivity extends AppCompatActivity {

    protected final BaseActivity self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
    }

    /**
     * Returns the resource ID of this activity's layout.
     *
     * @return the layout resource ID
     */
    protected abstract int getLayoutId();

    /**
     * A generic version of {@link #findViewById(int)} that automatically casts the returned view to the type of the
     * reference to which it is assigned. This cast is unchecked.
     *
     * @param resourceId resource ID of the view in the layout given by {@link #getLayoutId()}
     * @return the view if found or null otherwise
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T findView(int resourceId) {
        return (T) findViewById(resourceId);
    }

    /**
     * Variant of {@link #findView(int)} which never returns null if no view with the given ID is found. Instead, it
     * throws {@link IllegalStateException} in such case.
     *
     * @param resourceId resource ID of the view in the layout given by {@link #getLayoutId()}
     * @return the view, never null
     * @throws IllegalStateException if no view with the given ID is found
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T requireView(int resourceId) {
        T view = (T) findViewById(resourceId);
        if (view == null) {
            throw new IllegalStateException("View with ID " + resourceId + " not found.");
        }
        return view;
    }

    /**
     * <p>
     * Returns an extra value from this activity's intent or throws {@link IllegalStateException} if no value is
     * associated with the given key.
     *
     * <p>
     * The returned value is automatically casted to the type of the reference to which it is assigned. This cast is
     * unchecked.
     *
     * @param key key for the extra value
     * @return the extra value for the given key
     * @throws IllegalStateException if no value is present for the given key
     */
    @SuppressWarnings("unchecked")
    public <T> T requireIntentExtra(String key) {
        T value = (T) getIntent().getExtras().get(key);
        if (value == null) {
            throw new IllegalStateException("Missing intent extra for key: " + key);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public <T> T getIntentExtra(String key) {
        return (T) getIntent().getExtras().get(key);
    }

    /**
     * Starts a new activity of the given class. This is a convenient version of {@link #startActivity(Intent)} without
     * explicitly creating the intent.
     *
     * @param activityClass the activity to start
     */
    public void startActivity(Class<? extends Activity> activityClass) {
        startActivity(new Intent(this, activityClass));
    }

}
