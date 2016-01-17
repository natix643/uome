package cz.kns.uome.common.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import cz.kns.uome.common.activity.BaseActivity;

public abstract class BaseListFragment extends ListFragment {

	protected final BaseListFragment self = this;

	private BaseActivity baseActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		baseActivity = requireActivityIsInstanceOf(activity, BaseActivity.class);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		baseActivity = null;
	}

	public BaseActivity getBaseActivity() {
		return baseActivity;
	}

	protected <T extends BaseActivity> T requireActivityIsInstanceOf(Activity activity, Class<T> clazz) {
		return Fragments.requireActivityIsInstanceOf(this, activity, clazz);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initEmptyView();
	}

	private void initEmptyView() {
		View emptyView = findView(android.R.id.empty);
		if (emptyView != null) {
			Integer emptyTextId = getEmptyTextId();
			if (emptyTextId != null) {
				((TextView) emptyView).setText(emptyTextId);
			}
		}
	}

	protected Integer getEmptyTextId() {
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T findView(int resourceId) {
		return (T) getView().findViewById(resourceId);
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T requireView(int resourceId) {
		T view = (T) getView().findViewById(resourceId);
		if (view == null) {
			throw new IllegalStateException("View with ID " + resourceId + " not found.");
		}
		return view;
	}

	public <T> T requireArgument(String key) {
		return Fragments.requireArgument(this, key);
	}

	/**
	 * <p>
	 * Override {@link #onSupportContextItemSelected(MenuItem)} instead.
	 * 
	 * <p>
	 * This implementation ensures that the selected item really comes from this fragment. If it does, the call is
	 * delegated to {@link #onSupportContextItemSelected(MenuItem)}. If no, false is returned, so other fragments in
	 * this activity can try to handle the item.
	 */
	@Override
	public final boolean onContextItemSelected(MenuItem item) {
		return getUserVisibleHint() ? onSupportContextItemSelected(item) : false;
	}

	/**
	 * Override this method instead of {@link #onContextItemSelected(MenuItem)}. This will be called only when the
	 * selected context menu item really comes from this Fragment.
	 * 
	 * @param item the context menu item that was selected
	 * @return false to allow normal context menu processing to proceed, true to consume it here
	 */
	public boolean onSupportContextItemSelected(MenuItem item) {
		return false;
	}
}
