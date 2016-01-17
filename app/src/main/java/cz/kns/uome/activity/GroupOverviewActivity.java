package cz.kns.uome.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import java.util.List;

import cz.kns.uome.R;
import cz.kns.uome.common.Constants;
import cz.kns.uome.common.format.MoneyFormatter;
import cz.kns.uome.common.pager.Page;
import cz.kns.uome.common.util.Intents;
import cz.kns.uome.common.util.Toaster;
import cz.kns.uome.dialog.ConfirmationDialog;
import cz.kns.uome.fragment.GroupListTransactionsFragment;
import cz.kns.uome.fragment.ListBalancesFragment.GroupListBalancesFragment;
import cz.kns.uome.model.Group;
import cz.kns.uome.model.GroupDao;

public class GroupOverviewActivity extends OverviewActivity implements ConfirmationDialog.Callback {

    private static final String REQUEST_DELETE_GROUP = "deleteGroup";

    private final MoneyFormatter moneyFormatter = MoneyFormatter.withPlusPrefix();

    private Toaster toaster;

    private GroupDao groupDao;

    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toaster = new Toaster(this);
        groupDao = new GroupDao(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO can this really happen?
        group = groupDao.getById(getGroupId());
        if (group == null) {
            startActivity(Intents.openGroup(this, Constants.SIMPLE_GROUP_ID));
            finish();
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(group.getName());
        actionBar.setSubtitle(Strings.emptyToNull(group.getDescription()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_group:
                startActivity(Intents.editGroup(this, group));
                return true;
            case R.id.menu_delete_group:
                ConfirmationDialog.of(REQUEST_DELETE_GROUP)
                        .setTitle(R.string.dialog_delete_group_title)
                        .setMessage(R.string.dialog_delete_group_message)
                        .setPositiveButton(R.string.delete)
                        .show(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfirmed(String requestCode) {
        if (requestCode.equals(REQUEST_DELETE_GROUP)) {
            groupDao.delete(group);

            startActivity(SimpleOverviewActivity.class);
            finish();
            overridePendingTransition(0, 0);

            toaster.show(R.string.toast_group_deleted);
        }
    }

    @Override
    protected List<Page> getPages() {
        Bundle args = argsWithGroupId();
        return ImmutableList.of(
                new Page(GroupListBalancesFragment.class, args, R.string.tab_people),
                new Page(GroupListTransactionsFragment.class, args, R.string.tab_transactions));
    }

    private Bundle argsWithGroupId() {
        Bundle args = new Bundle();
        args.putLong(Constants.GROUP_ID, getGroupId());
        return args;
    }

    @Override
    protected long getGroupId() {
        return requireIntentExtra(Constants.GROUP_ID);
    }

    @Override
    protected MoneyFormatter getMoneyFormatter() {
        return moneyFormatter;
    }
}
