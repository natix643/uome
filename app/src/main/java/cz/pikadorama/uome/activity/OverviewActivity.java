package cz.pikadorama.uome.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.common.view.SlidingTabLayout;
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
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.format.MoneyFormatter;
import cz.pikadorama.uome.common.pager.BasePagerAdapter;
import cz.pikadorama.uome.common.pager.PagerActivity;
import cz.pikadorama.uome.common.util.Intents;
import cz.pikadorama.uome.common.util.ListViewUtil;
import cz.pikadorama.uome.fragment.OverviewFragment;
import cz.pikadorama.uome.model.Group;
import cz.pikadorama.uome.model.GroupDao;

import static com.google.common.base.Preconditions.checkState;

public abstract class OverviewActivity extends PagerActivity {

    private static final int REQUEST_ADD_GROUP = 19;

    private GroupDao groupDao;

    private NavigationAdapter navigationAdapter;

    private DrawerLayout drawerLayout;
    private ListView drawerListView;
    private ActionBarDrawerToggle drawerToggle;

    private TextView totalTextView;

    // TODO this is not really needed when MVC is used
    private BasePagerAdapter pagerAdapter;

    private NavigationListener onDrawerClosedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        groupDao = new GroupDao(this);
        navigationAdapter = new NavigationAdapter(this);

        Toolbar toolbar = requireView(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initTabs();

        initNavigationDrawer();

        totalTextView = findView(R.id.totalTextView);
    }

    private void initTabs() {
        SlidingTabLayout tabLayout = findView(R.id.sliding_tabs);
        tabLayout.setViewPager(getPager());
        tabLayout.setSelectedIndicatorColors(getResources().getColor(android.R.color.white));
        tabLayout.setDividerColors(getResources().getColor(android.R.color.transparent));
    }

    private void initNavigationDrawer() {
        drawerListView = findView(R.id.drawer_list_view);

        View header = getLayoutInflater().inflate(R.layout.drawer_header, drawerListView, false);
        drawerListView.addHeaderView(header, null, false);
        drawerListView.setAdapter(navigationAdapter);

        drawerListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NavigationItem item = (NavigationItem) drawerListView.getItemAtPosition(position);
                onDrawerClosedListener = item.getListener();
                drawerLayout.closeDrawer(Gravity.START);
            }
        });

        drawerLayout = findView(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        drawerToggle = createToggle(drawerLayout);
        drawerLayout.setDrawerListener(drawerToggle);
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
                        startActivityForResult(Intents.addGroup(self), REQUEST_ADD_GROUP);
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

        if (requestCode == REQUEST_ADD_GROUP && resultCode == RESULT_OK) {
            long groupId = data.getLongExtra(Constants.GROUP_ID, Constants.MISSING_EXTRA);
            checkState(groupId != Constants.MISSING_EXTRA, "Missing intent extra for key: " + Constants.GROUP_ID);

            startActivity(Intents.openGroup(this, groupId));
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putLong(Constants.PREF_LAST_OPENED_GROUP, getGroupId())
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
                if (drawerLayout.isDrawerVisible(Gravity.START)) {
                    drawerLayout.closeDrawer(Gravity.START);
                } else {
                    drawerLayout.openDrawer(Gravity.START);
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
                closeFragmentActionMode();
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

    private void closeFragmentActionMode() {
        int currentTab = getPager().getCurrentItem();
        OverviewFragment fragment = (OverviewFragment) getPagerAdapter().getFragment(currentTab);
        fragment.closeActionMode();
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
