package cz.pikadorama.uome.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.adapter.navigation.BasicItem;
import cz.pikadorama.uome.adapter.navigation.DividerItem;
import cz.pikadorama.uome.adapter.navigation.GroupItem;
import cz.pikadorama.uome.adapter.navigation.NavigationAdapter;
import cz.pikadorama.uome.adapter.navigation.NavigationItem;
import cz.pikadorama.uome.adapter.navigation.NavigationListener;
import cz.pikadorama.uome.common.ActivityRequest;
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.Event;
import cz.pikadorama.uome.common.format.MoneyFormatter;
import cz.pikadorama.uome.common.pager.BasePagerAdapter;
import cz.pikadorama.uome.common.pager.PagerActivity;
import cz.pikadorama.uome.common.util.Intents;
import cz.pikadorama.uome.common.util.ListViewUtil;
import cz.pikadorama.uome.common.util.SnackbarHelper;
import cz.pikadorama.uome.common.view.Animations;
import cz.pikadorama.uome.fragment.OverviewFragment;
import cz.pikadorama.uome.model.Group;
import cz.pikadorama.uome.model.GroupDao;

import static android.support.v4.view.GravityCompat.START;
import static com.google.common.base.Preconditions.checkState;

public abstract class OverviewActivity extends PagerActivity {

    private static final String PREFERENCE_CURRENT_PAGE = "currentPage";

    private GroupDao groupDao;

    private SnackbarHelper snackbarHelper;

    private NavigationAdapter navigationAdapter;

    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle drawerToggle;

    private FloatingActionButton addTransactionButton;
    private TextView totalTextView;

    // TODO this is not really needed when MVC is used
    private BasePagerAdapter pagerAdapter;

    private NavigationListener onDrawerClosedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        groupDao = new GroupDao(this);
        snackbarHelper = new SnackbarHelper(this);
        navigationAdapter = new NavigationAdapter(this);

        totalTextView = requireView(R.id.totalTextView);

        Toolbar toolbar = requireView(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initTabs();
        initNavigationDrawer();
        initFloatingButton();

        boolean firstStart = savedInstanceState == null;
        if (firstStart) {
            showRequestSnackbars();
        }
        selectPage(firstStart);
    }

    private void initTabs() {
        TabLayout tabLayout = requireView(R.id.tabs);
        tabLayout.setupWithViewPager(getPager());
    }

    private void initNavigationDrawer() {
        drawerListView = requireView(R.id.drawer_list_view);

        View header = getLayoutInflater().inflate(R.layout.drawer_header, drawerListView, false);
        drawerListView.addHeaderView(header, null, false);
        drawerListView.setAdapter(navigationAdapter);

        drawerListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NavigationItem item = (NavigationItem) drawerListView.getItemAtPosition(position);
                onDrawerClosedListener = item.getListener();
                drawerLayout.closeDrawer(START);
            }
        });

        drawerLayout = requireView(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, START);

        drawerToggle = createToggle(drawerLayout);
        drawerLayout.setDrawerListener(drawerToggle);
    }

    private void initFloatingButton() {
        addTransactionButton = requireView(R.id.floatingButton);
        addTransactionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(Intents.addTransaction(self, getGroupId()), ActivityRequest.ADD_TRANSACTION);
            }
        });
    }

    private void showRequestSnackbars() {
        if (getIntent().getBooleanExtra(Event.GROUP_ADDED, false)) {
            snackbarHelper.info(R.string.toast_group_added);
        }
        if (getIntent().getBooleanExtra(Event.GROUP_DELETED, false)) {
            snackbarHelper.info(R.string.toast_group_deleted);
        }
    }

    private void selectPage(boolean firstStart) {
        boolean newGroup = getIntent().getBooleanExtra(Event.GROUP_ADDED, false);

        if (newGroup && firstStart) {
            getPager().setCurrentItem(0);
        } else {
            int page = PreferenceManager.getDefaultSharedPreferences(this).getInt(PREFERENCE_CURRENT_PAGE, 0);
            getPager().setCurrentItem(page);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshNavigation();
    }

    private void refreshNavigation() {
        navigationAdapter.clear();

        navigationAdapter.add(new DividerItem());

        List<Group> groups = groupDao.getAllWithSimpleFirst();

        navigationAdapter.addAll(Lists.transform(groups, groupToItem));
        navigationAdapter.add(new BasicItem(R.string.menu_add_group, R.drawable.ic_action_add_group,
                new NavigationListener() {
                    @Override
                    public void onItemSelected() {
                        startActivityForResult(Intents.addGroup(self), ActivityRequest.ADD_GROUP);
                    }
                }));

        navigationAdapter.add(new DividerItem());

        navigationAdapter.add(new BasicItem(R.string.menu_settings, R.drawable.ic_drawer_settings,
                new NavigationListener() {
                    @Override
                    public void onItemSelected() {
                        startActivity(SettingsActivity.class);
                    }
                }));
        navigationAdapter.add(new BasicItem(R.string.menu_help, R.drawable.ic_drawer_help,
                new NavigationListener() {
                    @Override
                    public void onItemSelected() {
                        startActivity(HelpActivity.class);
                    }
                }));

        checkCurrentGroup();
    }

    private void checkCurrentGroup() {
        for (int i = 0; i < drawerListView.getCount(); i++) {
            Object item = drawerListView.getItemAtPosition(i);
            if (item instanceof GroupItem) {
                GroupItem groupItem = (GroupItem) item;

                if (groupItem.getGroup().getId() == getGroupId()) {
                    drawerListView.setItemChecked(i, true);
                    return;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ActivityRequest.ADD_GROUP:
                    long groupId = data.getLongExtra(Constants.GROUP_ID, Constants.MISSING_EXTRA);
                    checkState(groupId != Constants.MISSING_EXTRA);

                    Intent intent = Intents.openGroup(this, groupId)
                            .putExtra(Event.GROUP_ADDED, true);
                    startActivity(intent);
                    finish();
                    break;
                case ActivityRequest.ADD_TRANSACTION:
                    snackbarHelper.info(R.string.toast_transaction_added);
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putLong(Constants.PREF_LAST_OPENED_GROUP, getGroupId())
                .putInt(PREFERENCE_CURRENT_PAGE, getPager().getCurrentItem())
                .commit();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.overview;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayout.isDrawerVisible(START)) {
                    drawerLayout.closeDrawer(START);
                } else {
                    drawerLayout.openDrawer(START);
                }
                return true;
            default:
                return false;
        }
    }

    private ActionBarDrawerToggle createToggle(DrawerLayout drawer) {
        return new ActionBarDrawerToggle(
                this,
                drawer,
                R.string.content_description_open_drawer,
                R.string.content_description_close_drawer) {

            @Override
            public void onDrawerStateChanged(int i) {
                int currentTab = getPager().getCurrentItem();
                OverviewFragment fragment = (OverviewFragment) getPagerAdapter().getFragment(currentTab);
                fragment.closeActionMode();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                if (onDrawerClosedListener != null) {
                    onDrawerClosedListener.onItemSelected();
                    onDrawerClosedListener = null;
                }
            }
        };
    }

    public void onCreateActionMode() {
        Animations.collapse(addTransactionButton);
    }

    public void onDestroyActionMode() {
        Animations.expand(addTransactionButton);
    }

    @Override
    protected BasePagerAdapter createPagerAdapter() {
        pagerAdapter = new BasePagerAdapter(this, getPages());
        return pagerAdapter;
    }

    public BasePagerAdapter getPagerAdapter() {
        return pagerAdapter;
    }

    public void refreshTotalAmount(BigDecimal amount) {
        totalTextView.setText(getMoneyFormatter().format(amount));
        totalTextView.setTextColor(ListViewUtil.getAmountColor(this, amount));

        refreshNavigation();
    }

    private final Function<Group, GroupItem> groupToItem = new Function<Group, GroupItem>() {
        @Override
        public GroupItem apply(final Group group) {
            return new GroupItem(group, new NavigationListener() {
                @Override
                public void onItemSelected() {
                    if (group.getId() != getGroupId()) {
                        startActivity(Intents.openGroup(self, group));
                        finish();
                        overridePendingTransition(0, 0);
                    }
                }
            });
        }
    };

    protected abstract long getGroupId();

    protected abstract MoneyFormatter getMoneyFormatter();

}
