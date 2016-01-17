package cz.kns.uome.activity;

import com.google.common.collect.ImmutableList;

import java.util.List;

import cz.kns.uome.R;
import cz.kns.uome.common.Constants;
import cz.kns.uome.common.format.MoneyFormatter;
import cz.kns.uome.common.pager.Page;
import cz.kns.uome.fragment.ListBalancesFragment.SimpleListBalancesFragment;
import cz.kns.uome.fragment.SimpleListTransactionsFragment;

public class SimpleOverviewActivity extends OverviewActivity {

    private static final List<Page> PAGES = ImmutableList.of(
            new Page(SimpleListBalancesFragment.class, R.string.tab_people),
            new Page(SimpleListTransactionsFragment.class, R.string.tab_transactions));

    private final MoneyFormatter moneyFormatter = MoneyFormatter.withoutPlusPrefix();

    @Override
    protected List<Page> getPages() {
        return PAGES;
    }

    @Override
    protected long getGroupId() {
        return Constants.SIMPLE_GROUP_ID;
    }

    @Override
    protected MoneyFormatter getMoneyFormatter() {
        return moneyFormatter;
    }
}
