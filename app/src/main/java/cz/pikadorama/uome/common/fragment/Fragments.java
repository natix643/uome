package cz.pikadorama.uome.common.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import cz.pikadorama.uome.common.activity.BaseActivity;

public class Fragments {

    private Fragments() {}

    /**
     * <p>
     * Returns an argument for the given fragment or throws {@link IllegalStateException} if no argument is associated
     * with the given key.
     *
     * <p>
     * The returned value is automatically casted to the type of the reference to which it is assigned. This cast is
     * unchecked.
     *
     * <p>
     * This method is basically a static implementation of instance methods such as
     * {@link BaseListFragment#requireArgument(String)}.
     *
     * @param fragment a fragment, not null
     * @param key key for the argument
     * @return the argument for the given key
     * @throws IllegalStateException if no argument is present for the given key
     */
    @SuppressWarnings("unchecked")
    public static <T> T requireArgument(Fragment fragment, String key) {
        T value = (T) fragment.getArguments().get(key);
        if (value == null) {
            throw new IllegalStateException("Missing argument for key: " + key);
        }
        return value;
    }

    /**
     * Like {@link #instantiate(Context, Class, Bundle)} but with a null argument Bundle.
     */
    public static <T extends Fragment> T instantiate(Context context, Class<T> fragmentClass) {
        return instantiate(context, fragmentClass, null);
    }

    /**
     * Generic and type-safe version of {@link Fragment#instantiate(Context, String, Bundle)} which accepts directly a
     * class of the fragment instead of just class name. It also automatically casts the instantiated fragment to that
     * class.
     *
     * @param context The calling context being used to instantiate the fragment. This is currently just used to get its
     *            ClassLoader.
     * @param fragmentClass the class of the fragment to instantiate
     * @param arguments Bundle of arguments to supply to the fragment, which it can retrieve with
     *            {@link Fragment#getArguments()}. May be null.
     * @return a new fragment instance, casted to the given class
     * @throws InstantiationException If there is a failure in instantiating the given fragment class. This is a runtime
     *             exception; it is not normally expected to happen.
     */
    public static <T extends Fragment> T instantiate(Context context, Class<T> fragmentClass, Bundle arguments) {
        return fragmentClass.cast(Fragment.instantiate(context, fragmentClass.getName(), arguments));
    }

    /**
     * <p>
     * Casts the given activity to the given class or throws an exception if the activity isn't an instance of that
     * class.
     *
     * <p>
     * The use case for this method is to ensure that a fragment is attached only to an activity of a given class, which
     * is typically performed in its {@link Fragment#onAttach(Activity) onAttach(Activity)} method.
     *
     * @param fragment fragment that requires to be attached to an instance of {@code activityClass}. Its class is used
     *            as a part of the exception message.
     * @param activity activity that {@code fragment} is to be attached to
     * @param activityClass class that {@code activity} must be instance of
     * @return {@code activity} casted to {@code activityClass}
     * @throws ClassCastException if {@code activity} is not instance of {@code activityClass}
     */
    public static <T extends BaseActivity> T requireActivityIsInstanceOf(Fragment fragment, Activity activity,
            Class<T> activityClass) {
        if (!activityClass.isInstance(activity)) {
            throw new ClassCastException(
                    fragment.getClass().getSimpleName() + " must be attached to a " + activityClass.getSimpleName());
        }
        return activityClass.cast(activity);
    }

}
