package cz.pikadorama.uome.fragment;

import android.app.Activity;
import android.view.ActionMode;

import cz.pikadorama.uome.activity.OverviewActivity;
import cz.pikadorama.uome.common.fragment.BaseListFragment;

public abstract class OverviewFragment extends BaseListFragment {

    private OverviewActivity overviewActivity;

    protected ActionMode actionMode;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        overviewActivity = requireActivityIsInstanceOf(activity, OverviewActivity.class);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        overviewActivity = null;
    }

    @Override
    public OverviewActivity getBaseActivity() {
        return overviewActivity;
    }

    public void closeActionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }
}
