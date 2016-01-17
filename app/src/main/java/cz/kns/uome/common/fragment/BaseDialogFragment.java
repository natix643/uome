package cz.kns.uome.common.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.support.v4.app.FragmentActivity;

public class BaseDialogFragment extends DialogFragment {

    /**
     * Convenient version of {@link #show(FragmentManager, String)} which automatically uses the fully qualified name of
     * this fragment's class as the tag.
     *
     * @param manager the FragmentManager this fragment will be added to
     */
    public void show(FragmentManager manager) {
        show(manager, getClass().getName());
    }

    /**
     * Convenient version of {@link #show(FragmentManager, String)} which automatically uses the {@link FragmentManager}
     * provided by the given {@link FragmentActivity}. The fully qualified name of this fragment's class is used as the
     * tag.
     *
     * @param activity FragmentActivity to whose FragmentManager this fragment will be added to
     */
    public void show(Activity activity) {
        show(activity.getFragmentManager());
    }

    /**
     * <p>
     * Returns an argument for this fragment or throws {@link IllegalStateException} if no argument is associated with
     * the given key.
     *
     * <p>
     * The returned value is automatically casted to the type of the reference to which it is assigned. This cast is
     * unchecked.
     *
     * @param key key for the argument
     * @return the argument for the given key
     * @throws IllegalStateException if no argument is present for the given key
     */
    public <T> T requireArgument(String key) {
        return Fragments.requireArgument(this, key);
    }

}
