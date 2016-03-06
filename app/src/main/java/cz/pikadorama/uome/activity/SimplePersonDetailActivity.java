package cz.pikadorama.uome.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.madgag.android.listviews.ViewHoldingListAdapter;

import java.math.BigDecimal;
import java.util.List;

import cz.pikadorama.uome.R;
import cz.pikadorama.uome.adapter.viewholder.PersonTransactionViewHolder;
import cz.pikadorama.uome.common.format.MoneyFormatter;
import cz.pikadorama.uome.common.util.ListViewUtil;
import cz.pikadorama.uome.common.util.TransactionFilter;
import cz.pikadorama.uome.model.Transaction;

import static cz.pikadorama.uome.common.util.TransactionFilter.*;

public class SimplePersonDetailActivity extends PersonDetailActivity {

    private static final String KEY_FILTER = "filter";

    private final MoneyFormatter moneyFormatter = MoneyFormatter.withoutPlusPrefix();

    private TransactionFilter filter = TransactionFilter.ALL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        this.filter = (TransactionFilter) savedInstanceState.getSerializable(KEY_FILTER);
        invalidateOptionsMenu();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_FILTER, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        filter.prepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // override the default behaviour when this activity
            // was started from the widget
            case android.R.id.home:
                startActivity(StartupActivity.class);
                finish();
                return true;
            case R.id.menu_filter_all:
                selectFilter(ALL);
                return true;
            case R.id.menu_filter_financial:
                selectFilter(FINANCIAL);
                return true;
            case R.id.menu_filter_non_financial:
                selectFilter(NON_FINANCIAL);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectFilter(TransactionFilter newFilter) {
        this.filter = newFilter;
        invalidateOptionsMenu();
        refreshTransactions();
    }

    @Override
    protected ViewHoldingListAdapter<Transaction> createAdapter() {
        return PersonTransactionViewHolder.forSimpleDebts(this);
    }

    @Override
    protected void refreshTransactions() {
        List<Transaction> allTransactions = loadTransactions();
        getAdapter().setList(filter.doFilter(allTransactions));
        refreshTransactionsSummary(allTransactions);
    }

    @Override
    protected void refreshTransactionsSummary(BigDecimal amount) {
        bottomHintText.setText(ListViewUtil.getHintForBalance(amount));

        bottomAmountText.setVisibility(amount.compareTo(BigDecimal.ZERO) == 0 ? View.GONE : View.VISIBLE);
        bottomAmountText.setText(moneyFormatter.format(amount.abs()));
    }

    @Override
    protected boolean canEditTransactions() {
        return true;
    }

}
