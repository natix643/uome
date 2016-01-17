package cz.kns.uome.common.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;

/**
 * <p>
 * Base class for {@link DialogFragment} that can be shown either from an Activity (using {@link #show(Activity)}) or
 * from a Fragment (using {@link #show(Fragment)}).
 * 
 * <p>
 * Such activity or fragment is then available in the respective callback method: {@link #onAttachToActivity(Activity)}
 * or {@link #onAttachToFragment(Fragment)}.
 */
public abstract class DualDialogFragment extends BaseDialogFragment {

	private static final String ARG_CALLBACK_FRAGMENT_TAG = "callbackFragmentTag";

	public void show(Fragment fragment) {
		if (getArguments() == null) {
			setArguments(new Bundle());
		}

		getArguments().putString(ARG_CALLBACK_FRAGMENT_TAG, fragment.getTag());
		show(fragment.getFragmentManager());
	}

	@Override
	public final void onAttach(Activity activity) {
		super.onAttach(activity);

		String callbackFragmentTag = getArguments().getString(ARG_CALLBACK_FRAGMENT_TAG);
		if (callbackFragmentTag != null) {
			onAttachToFragment(requireCallbackFragment(callbackFragmentTag));
		} else {
			onAttachToActivity(activity);
		}
	}

	private Fragment requireCallbackFragment(String tag) {
		Fragment fragment = getFragmentManager().findFragmentByTag(tag);
		if (fragment == null) {
			throw new IllegalStateException("Missing required callback fragment with tag: " + tag);
		}
		return fragment;
	}

	/**
	 * This method is called from {@link #onAttach(Activity)} only when this dialog fragment is being shown from an
	 * activity. Subclasses may freely override.
	 * 
	 * @param activity the activity from which this dialog fragment was shown
	 */
	protected void onAttachToActivity(Activity activity) {}

	/**
	 * This method is called from {@link #onAttach(Activity)} only when this dialog fragment is being shown from a
	 * fragment. Subclasses may freely override.
	 * 
	 * @param fragment the not-null fragment from which this dialog fragment was shown
	 */
	protected void onAttachToFragment(Fragment fragment) {}
}
