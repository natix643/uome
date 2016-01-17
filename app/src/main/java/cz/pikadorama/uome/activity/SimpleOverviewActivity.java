package cz.pikadorama.uome.activity;

import com.google.common.collect.ImmutableList;

import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.common.Constants;
import cz.pikadorama.uome.common.format.MoneyFormatter;
import cz.pikadorama.uome.common.pager.Page;
import cz.pikadorama.uome.fragment.ListBalancesFragment.SimpleListBalancesFragment;
import cz.pikadorama.uome.fragment.SimpleListTransactionsFragment;

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
